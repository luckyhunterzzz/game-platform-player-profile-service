package com.gameplatform.playerprofileservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameplatform.playerprofileservice.domain.entity.PlayerProfile;
import com.gameplatform.playerprofileservice.domain.entity.PlayerProfileHero;
import com.gameplatform.playerprofileservice.domain.entity.WarMode;
import com.gameplatform.playerprofileservice.domain.entity.WarStatAttackRecord;
import com.gameplatform.playerprofileservice.domain.entity.WarStatAttackTeam;
import com.gameplatform.playerprofileservice.domain.entity.WarStatAttackTeamSlot;
import com.gameplatform.playerprofileservice.domain.entity.TeamWarAttack;
import com.gameplatform.playerprofileservice.domain.entity.TeamWarAttackHero;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatAttackImportRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatAttackRecordUpsertRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatAttackTeamSlotUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatAttackTeamUpdateRequestDto;
import com.gameplatform.playerprofileservice.repository.PlayerProfileHeroRepository;
import com.gameplatform.playerprofileservice.repository.TeamWarAttackHeroRepository;
import com.gameplatform.playerprofileservice.repository.TeamWarAttackRepository;
import com.gameplatform.playerprofileservice.repository.WarModeRepository;
import com.gameplatform.playerprofileservice.repository.WarStatAttackRecordRepository;
import com.gameplatform.playerprofileservice.repository.WarStatAttackTeamRepository;
import com.gameplatform.playerprofileservice.repository.WarStatAttackTeamSlotRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerWarStatAttackTeamService {

    private static final int TEAM_SIZE = 5;

    private final PlayerProfileService playerProfileService;
    private final PlayerProfileHeroRepository playerProfileHeroRepository;
    private final TeamWarAttackRepository teamWarAttackRepository;
    private final TeamWarAttackHeroRepository teamWarAttackHeroRepository;
    private final WarStatAttackTeamRepository warStatAttackTeamRepository;
    private final WarStatAttackTeamSlotRepository warStatAttackTeamSlotRepository;
    private final WarStatAttackRecordRepository warStatAttackRecordRepository;
    private final WarModeRepository warModeRepository;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    @Transactional
    public WarStatAttackTeamsView getMyTeams(UUID userId, String email) {
        PlayerProfile playerProfile = playerProfileService.getOrCreateProfile(userId, email);
        List<WarMode> warModes = getStatisticWarModes();
        List<WarStatAttackTeam> teams = warStatAttackTeamRepository.findAllByPlayerProfileIdOrderByTeamOrderAsc(playerProfile.getId());
        return buildTeamsView(warModes, teams);
    }

    @Transactional
    public WarStatAttackTeamsView createTeam(UUID userId, String email) {
        PlayerProfile playerProfile = playerProfileService.getOrCreateProfile(userId, email);
        List<WarMode> warModes = getStatisticWarModes();
        List<WarStatAttackTeam> existingTeams = warStatAttackTeamRepository.findAllByPlayerProfileIdOrderByTeamOrderAsc(playerProfile.getId());

        int nextOrder = existingTeams.stream()
                .map(WarStatAttackTeam::getTeamOrder)
                .max(Integer::compareTo)
                .orElse(0) + 1;

        OffsetDateTime now = OffsetDateTime.now(clock);
        warStatAttackTeamRepository.save(WarStatAttackTeam.builder()
                .id(UUID.randomUUID())
                .playerProfileId(playerProfile.getId())
                .name("Team " + nextOrder)
                .teamOrder(nextOrder)
                .createdAt(now)
                .updatedAt(now)
                .build());

        return buildTeamsView(warModes, warStatAttackTeamRepository.findAllByPlayerProfileIdOrderByTeamOrderAsc(playerProfile.getId()));
    }

    @Transactional
    public WarStatAttackTeamsView importWarTeam(UUID userId,
                                                String email,
                                                PlayerWarStatAttackImportRequestDto request) {
        PlayerProfile playerProfile = playerProfileService.getOrCreateProfile(userId, email);
        List<WarMode> allWarModes = getActiveWarModes();
        List<WarMode> statisticWarModes = getStatisticWarModes();
        WarMode requestedWarMode = resolveWarMode(request.warModeCode(), allWarModes);
        List<TeamWarAttack> warTeams = teamWarAttackRepository.findAllByPlayerProfileId(playerProfile.getId());

        TeamWarAttack sourceTeam = warTeams.stream()
                .filter(team -> team.getWarModeId().equals(requestedWarMode.getId()) && team.getTeamIndex().intValue() == request.teamIndex())
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "War team not found"));

        List<TeamWarAttackHero> sourceHeroes = teamWarAttackHeroRepository.findAllByTeamWarAttackIdIn(List.of(sourceTeam.getId()));
        Map<Short, UUID> slotMap = sourceHeroes.stream()
                .collect(Collectors.toMap(TeamWarAttackHero::getSlot, TeamWarAttackHero::getPlayerProfileHeroId));

        List<UUID> importedProfileHeroIds = new ArrayList<>(TEAM_SIZE);
        for (short slot = 1; slot <= TEAM_SIZE; slot++) {
            importedProfileHeroIds.add(slotMap.get(slot));
        }

        if (importedProfileHeroIds.stream().allMatch(id -> id == null)) {
            throw new ResponseStatusException(BAD_REQUEST, "Cannot import an empty war team");
        }

        List<WarStatAttackTeam> existingStatTeams = warStatAttackTeamRepository.findAllByPlayerProfileIdOrderByTeamOrderAsc(playerProfile.getId());
        ensureNoDuplicateComposition(playerProfile.getId(), null, importedProfileHeroIds, existingStatTeams);

        int nextOrder = existingStatTeams.stream()
                .map(WarStatAttackTeam::getTeamOrder)
                .max(Integer::compareTo)
                .orElse(0) + 1;

        OffsetDateTime now = OffsetDateTime.now(clock);
        WarStatAttackTeam createdTeam = warStatAttackTeamRepository.save(WarStatAttackTeam.builder()
                .id(UUID.randomUUID())
                .playerProfileId(playerProfile.getId())
                .name("Team " + nextOrder)
                .teamOrder(nextOrder)
                .createdAt(now)
                .updatedAt(now)
                .build());

        List<WarStatAttackTeamSlot> slotsToSave = new ArrayList<>();
        for (short slot = 1; slot <= TEAM_SIZE; slot++) {
            UUID playerProfileHeroId = slotMap.get(slot);
            if (playerProfileHeroId == null) {
                continue;
            }

            slotsToSave.add(WarStatAttackTeamSlot.builder()
                    .id(UUID.randomUUID())
                    .teamId(createdTeam.getId())
                    .slot(slot)
                    .playerProfileHeroId(playerProfileHeroId)
                    .createdAt(now)
                    .build());
        }

        if (!slotsToSave.isEmpty()) {
            warStatAttackTeamSlotRepository.saveAll(slotsToSave);
        }

        return buildTeamsView(statisticWarModes, warStatAttackTeamRepository.findAllByPlayerProfileIdOrderByTeamOrderAsc(playerProfile.getId()));
    }

    @Transactional
    public WarStatAttackTeamsView updateTeam(UUID userId,
                                             String email,
                                             UUID teamId,
                                             PlayerWarStatAttackTeamUpdateRequestDto request) {
        PlayerProfile playerProfile = playerProfileService.getOrCreateProfile(userId, email);
        List<WarMode> warModes = getStatisticWarModes();
        WarStatAttackTeam team = getTeamOrThrow(playerProfile.getId(), teamId);
        ensureTeamIsEditable(team);

        validateSlotsRequest(playerProfile.getId(), request);

        List<WarStatAttackTeam> existingTeams = warStatAttackTeamRepository.findAllByPlayerProfileIdOrderByTeamOrderAsc(playerProfile.getId());
        ensureNoDuplicateComposition(
                playerProfile.getId(),
                team.getId(),
                buildOrderedProfileHeroIds(request.slots()),
                existingTeams
        );

        warStatAttackTeamSlotRepository.deleteAllByTeamId(team.getId());
        warStatAttackTeamSlotRepository.flush();

        OffsetDateTime now = OffsetDateTime.now(clock);
        List<WarStatAttackTeamSlot> slotsToSave = request.slots().stream()
                .filter(slot -> slot.playerProfileHeroId() != null)
                .map(slot -> WarStatAttackTeamSlot.builder()
                        .id(UUID.randomUUID())
                        .teamId(team.getId())
                        .slot(slot.slot().shortValue())
                        .playerProfileHeroId(slot.playerProfileHeroId())
                        .createdAt(now)
                        .build())
                .toList();

        if (!slotsToSave.isEmpty()) {
            warStatAttackTeamSlotRepository.saveAll(slotsToSave);
        }

        team.setName(request.name().trim());
        team.setUpdatedAt(now);
        warStatAttackTeamRepository.save(team);

        return buildTeamsView(warModes, warStatAttackTeamRepository.findAllByPlayerProfileIdOrderByTeamOrderAsc(playerProfile.getId()));
    }

    @Transactional
    public WarStatAttackTeamsView deleteTeam(UUID userId, String email, UUID teamId) {
        PlayerProfile playerProfile = playerProfileService.getOrCreateProfile(userId, email);
        List<WarMode> warModes = getStatisticWarModes();
        WarStatAttackTeam team = getTeamOrThrow(playerProfile.getId(), teamId);
        warStatAttackTeamRepository.delete(team);
        return buildTeamsView(warModes, warStatAttackTeamRepository.findAllByPlayerProfileIdOrderByTeamOrderAsc(playerProfile.getId()));
    }

    @Transactional
    public WarStatAttackTeamsView createRecord(UUID userId,
                                               String email,
                                               UUID teamId,
                                               PlayerWarStatAttackRecordUpsertRequestDto request) {
        PlayerProfile playerProfile = playerProfileService.getOrCreateProfile(userId, email);
        List<WarMode> warModes = getStatisticWarModes();
        WarStatAttackTeam team = getTeamOrThrow(playerProfile.getId(), teamId);
        ensureTeamHasHeroes(team.getId());
        WarMode warMode = resolveWarMode(request.warModeCode(), warModes);
        OffsetDateTime now = OffsetDateTime.now(clock);

        warStatAttackRecordRepository.save(WarStatAttackRecord.builder()
                .id(UUID.randomUUID())
                .teamId(team.getId())
                .warModeId(warMode.getId())
                .resultType(request.resultType())
                .battleDate(request.battleDate())
                .createdAt(now)
                .updatedAt(now)
                .build());

        team.setUpdatedAt(now);
        warStatAttackTeamRepository.save(team);

        return buildTeamsView(warModes, warStatAttackTeamRepository.findAllByPlayerProfileIdOrderByTeamOrderAsc(playerProfile.getId()));
    }

    @Transactional
    public WarStatAttackTeamsView updateRecord(UUID userId,
                                               String email,
                                               UUID teamId,
                                               UUID recordId,
                                               PlayerWarStatAttackRecordUpsertRequestDto request) {
        PlayerProfile playerProfile = playerProfileService.getOrCreateProfile(userId, email);
        List<WarMode> warModes = getStatisticWarModes();
        WarStatAttackTeam team = getTeamOrThrow(playerProfile.getId(), teamId);
        WarStatAttackRecord record = warStatAttackRecordRepository.findByIdAndTeamId(recordId, team.getId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "War statistic record not found"));
        WarMode warMode = resolveWarMode(request.warModeCode(), warModes);
        OffsetDateTime now = OffsetDateTime.now(clock);

        record.setWarModeId(warMode.getId());
        record.setResultType(request.resultType());
        record.setBattleDate(request.battleDate());
        record.setUpdatedAt(now);
        warStatAttackRecordRepository.save(record);

        team.setUpdatedAt(now);
        warStatAttackTeamRepository.save(team);

        return buildTeamsView(warModes, warStatAttackTeamRepository.findAllByPlayerProfileIdOrderByTeamOrderAsc(playerProfile.getId()));
    }

    @Transactional
    public WarStatAttackTeamsView deleteRecord(UUID userId, String email, UUID teamId, UUID recordId) {
        PlayerProfile playerProfile = playerProfileService.getOrCreateProfile(userId, email);
        List<WarMode> warModes = getStatisticWarModes();
        WarStatAttackTeam team = getTeamOrThrow(playerProfile.getId(), teamId);
        WarStatAttackRecord record = warStatAttackRecordRepository.findByIdAndTeamId(recordId, team.getId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "War statistic record not found"));
        OffsetDateTime now = OffsetDateTime.now(clock);

        warStatAttackRecordRepository.delete(record);
        team.setUpdatedAt(now);
        warStatAttackTeamRepository.save(team);

        return buildTeamsView(warModes, warStatAttackTeamRepository.findAllByPlayerProfileIdOrderByTeamOrderAsc(playerProfile.getId()));
    }

    private WarStatAttackTeamsView buildTeamsView(List<WarMode> warModes, List<WarStatAttackTeam> teams) {
        List<UUID> teamIds = teams.stream()
                .map(WarStatAttackTeam::getId)
                .toList();

        List<WarStatAttackTeamSlot> teamSlots = teamIds.isEmpty()
                ? List.of()
                : warStatAttackTeamSlotRepository.findAllByTeamIdIn(teamIds);

        List<WarStatAttackRecord> records = teamIds.isEmpty()
                ? List.of()
                : warStatAttackRecordRepository.findAllByTeamIdIn(teamIds);

        Map<UUID, Map<Short, UUID>> heroByTeamAndSlot = new HashMap<>();
        for (WarStatAttackTeamSlot teamSlot : teamSlots) {
            heroByTeamAndSlot
                    .computeIfAbsent(teamSlot.getTeamId(), ignored -> new HashMap<>())
                    .put(teamSlot.getSlot(), teamSlot.getPlayerProfileHeroId());
        }

        Map<UUID, List<WarStatAttackRecord>> recordsByTeam = records.stream()
                .collect(Collectors.groupingBy(WarStatAttackRecord::getTeamId));

        Map<UUID, WarMode> warModeById = warModes.stream()
                .collect(Collectors.toMap(WarMode::getId, Function.identity()));

        List<WarModeView> warModeViews = warModes.stream()
                .map(this::toWarModeView)
                .toList();

        List<WarStatAttackTeamView> teamViews = teams.stream()
                .sorted(Comparator.comparing(WarStatAttackTeam::getTeamOrder))
                .map(team -> {
                    Map<Short, UUID> slotMap = heroByTeamAndSlot.getOrDefault(team.getId(), Map.of());
                    List<WarStatAttackSlotView> slots = new ArrayList<>(TEAM_SIZE);
                    for (short slot = 1; slot <= TEAM_SIZE; slot++) {
                        slots.add(WarStatAttackSlotView.builder()
                                .slot((int) slot)
                                .playerProfileHeroId(slotMap.get(slot))
                                .build());
                    }

                    List<WarStatAttackRecordView> recordViews = recordsByTeam.getOrDefault(team.getId(), List.of()).stream()
                            .sorted(Comparator.comparing(WarStatAttackRecord::getBattleDate).reversed()
                                    .thenComparing(WarStatAttackRecord::getCreatedAt).reversed())
                            .map(record -> {
                                WarMode warMode = warModeById.get(record.getWarModeId());
                                String warModeCode = warMode != null ? warMode.getCode() : "UNIVERSAL";
                                return WarStatAttackRecordView.builder()
                                        .id(record.getId())
                                        .warModeCode(warModeCode)
                                        .resultType(record.getResultType())
                                        .battleDate(record.getBattleDate())
                                        .build();
                            })
                            .toList();

                    return WarStatAttackTeamView.builder()
                            .id(team.getId())
                            .name(team.getName())
                            .teamOrder(team.getTeamOrder())
                            .slots(slots)
                            .records(recordViews)
                            .build();
                })
                .toList();

        return WarStatAttackTeamsView.builder()
                .warModes(warModeViews)
                .teams(teamViews)
                .build();
    }

    private void validateSlotsRequest(UUID playerProfileId, PlayerWarStatAttackTeamUpdateRequestDto request) {
        Set<Integer> slots = new HashSet<>();
        Set<UUID> usedProfileHeroIds = new HashSet<>();
        Set<UUID> requestedProfileHeroIds = new HashSet<>();

        for (PlayerWarStatAttackTeamSlotUpdateRequestDto slot : request.slots()) {
            if (!slots.add(slot.slot())) {
                throw new ResponseStatusException(BAD_REQUEST, "Duplicate slot: " + slot.slot());
            }

            UUID playerProfileHeroId = slot.playerProfileHeroId();
            if (playerProfileHeroId == null) {
                continue;
            }

            if (!usedProfileHeroIds.add(playerProfileHeroId)) {
                throw new ResponseStatusException(BAD_REQUEST, "Profile hero is already used in this statistic team: " + playerProfileHeroId);
            }

            requestedProfileHeroIds.add(playerProfileHeroId);
        }

        validateProfileHeroOwnership(playerProfileId, requestedProfileHeroIds);
    }

    private List<UUID> buildOrderedProfileHeroIds(List<PlayerWarStatAttackTeamSlotUpdateRequestDto> slots) {
        Map<Integer, UUID> slotMap = new HashMap<>();
        for (PlayerWarStatAttackTeamSlotUpdateRequestDto slot : slots) {
            slotMap.put(slot.slot(), slot.playerProfileHeroId());
        }

        List<UUID> orderedIds = new ArrayList<>(TEAM_SIZE);
        for (int slot = 1; slot <= TEAM_SIZE; slot++) {
            orderedIds.add(slotMap.get(slot));
        }
        return orderedIds;
    }

    private void ensureNoDuplicateComposition(UUID playerProfileId,
                                              UUID currentTeamId,
                                              List<UUID> candidateOrderedIds,
                                              List<WarStatAttackTeam> existingTeams) {
        String candidateKey = buildCompositionKey(candidateOrderedIds);
        if (candidateKey == null) {
            return;
        }

        List<UUID> teamIds = existingTeams.stream()
                .map(WarStatAttackTeam::getId)
                .toList();
        List<WarStatAttackTeamSlot> existingSlots = teamIds.isEmpty()
                ? List.of()
                : warStatAttackTeamSlotRepository.findAllByTeamIdIn(teamIds);

        Map<UUID, Map<Short, UUID>> slotMapByTeamId = new HashMap<>();
        for (WarStatAttackTeamSlot existingSlot : existingSlots) {
            slotMapByTeamId
                    .computeIfAbsent(existingSlot.getTeamId(), ignored -> new HashMap<>())
                    .put(existingSlot.getSlot(), existingSlot.getPlayerProfileHeroId());
        }

        for (WarStatAttackTeam existingTeam : existingTeams) {
            if (currentTeamId != null && existingTeam.getId().equals(currentTeamId)) {
                continue;
            }

            List<UUID> orderedIds = new ArrayList<>(TEAM_SIZE);
            Map<Short, UUID> slotMap = slotMapByTeamId.getOrDefault(existingTeam.getId(), Map.of());
            for (short slot = 1; slot <= TEAM_SIZE; slot++) {
                orderedIds.add(slotMap.get(slot));
            }

            String existingKey = buildCompositionKey(orderedIds);
            if (candidateKey.equals(existingKey)) {
                throw new ResponseStatusException(BAD_REQUEST, "This war statistic team already exists");
            }
        }
    }

    private String buildCompositionKey(List<UUID> orderedIds) {
        boolean hasAnyHero = orderedIds.stream().anyMatch(id -> id != null);
        if (!hasAnyHero) {
            return null;
        }

        return orderedIds.stream()
                .map(id -> id == null ? "-" : id.toString())
                .collect(Collectors.joining("|"));
    }

    private void validateProfileHeroOwnership(UUID playerProfileId, Set<UUID> requestedProfileHeroIds) {
        if (requestedProfileHeroIds.isEmpty()) {
            return;
        }

        List<PlayerProfileHero> profileHeroes = playerProfileHeroRepository.findAllById(requestedProfileHeroIds);
        if (profileHeroes.size() != requestedProfileHeroIds.size()) {
            throw new ResponseStatusException(BAD_REQUEST, "Some profile heroes do not exist");
        }

        boolean hasForeignHero = profileHeroes.stream()
                .anyMatch(profileHero -> !playerProfileId.equals(profileHero.getPlayerProfileId()));

        if (hasForeignHero) {
            throw new ResponseStatusException(BAD_REQUEST, "Some profile heroes do not belong to current profile");
        }
    }

    private WarStatAttackTeam getTeamOrThrow(UUID playerProfileId, UUID teamId) {
        return warStatAttackTeamRepository.findByIdAndPlayerProfileId(teamId, playerProfileId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "War statistic team not found"));
    }

    private void ensureTeamIsEditable(WarStatAttackTeam team) {
        if (warStatAttackRecordRepository.existsByTeamId(team.getId())) {
            throw new ResponseStatusException(BAD_REQUEST, "War statistic team is locked after first battle record");
        }
    }

    private void ensureTeamHasHeroes(UUID teamId) {
        boolean hasAnyHero = warStatAttackTeamSlotRepository.findAllByTeamId(teamId).stream()
                .anyMatch(slot -> slot.getPlayerProfileHeroId() != null);
        if (!hasAnyHero) {
            throw new ResponseStatusException(BAD_REQUEST, "Cannot create record for empty war statistic team");
        }
    }

    private List<WarMode> getActiveWarModes() {
        List<WarMode> warModes = warModeRepository.findAllByActiveTrueOrderBySortOrderAsc();
        if (warModes.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "No active war modes configured");
        }
        return warModes;
    }

    private List<WarMode> getStatisticWarModes() {
        return getActiveWarModes().stream()
                .filter(warMode -> !"UNIVERSAL".equalsIgnoreCase(warMode.getCode()))
                .toList();
    }

    private WarMode resolveWarMode(String warModeCode, List<WarMode> warModes) {
        String normalizedCode = normalizeModeCode(warModeCode);
        return warModes.stream()
                .filter(warMode -> normalizedCode.equals(warMode.getCode()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Unknown war mode code: " + warModeCode));
    }

    private String normalizeModeCode(String warModeCode) {
        return warModeCode.trim().toUpperCase();
    }

    private WarModeView toWarModeView(WarMode warMode) {
        Map<String, String> names = parseLocalizedJson(warMode.getNameJson());
        Map<String, String> descriptions = parseLocalizedJson(warMode.getDescriptionJson());

        return WarModeView.builder()
                .code(warMode.getCode())
                .nameRu(names.getOrDefault("ru", warMode.getCode()))
                .nameEn(names.getOrDefault("en", warMode.getCode()))
                .descriptionRu(descriptions.getOrDefault("ru", ""))
                .descriptionEn(descriptions.getOrDefault("en", ""))
                .sortOrder((int) warMode.getSortOrder())
                .build();
    }

    private Map<String, String> parseLocalizedJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to parse war mode localization JSON", exception);
        }
    }

    @Builder
    public record WarModeView(
            String code,
            String nameRu,
            String nameEn,
            String descriptionRu,
            String descriptionEn,
            Integer sortOrder
    ) {
    }

    @Builder
    public record WarStatAttackSlotView(
            Integer slot,
            UUID playerProfileHeroId
    ) {
    }

    @Builder
    public record WarStatAttackRecordView(
            UUID id,
            String warModeCode,
            com.gameplatform.playerprofileservice.domain.enums.WarStatAttackResultType resultType,
            java.time.LocalDate battleDate
    ) {
    }

    @Builder
    public record WarStatAttackTeamView(
            UUID id,
            String name,
            Integer teamOrder,
            List<WarStatAttackSlotView> slots,
            List<WarStatAttackRecordView> records
    ) {
    }

    @Builder
    public record WarStatAttackTeamsView(
            List<WarModeView> warModes,
            List<WarStatAttackTeamView> teams
    ) {
    }
}
