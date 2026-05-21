package com.gameplatform.playerprofileservice.service;

import com.gameplatform.playerprofileservice.domain.entity.PlayerProfile;
import com.gameplatform.playerprofileservice.domain.entity.PlayerProfileHero;
import com.gameplatform.playerprofileservice.domain.entity.TeamWarAttack;
import com.gameplatform.playerprofileservice.domain.entity.TeamWarAttackHero;
import com.gameplatform.playerprofileservice.domain.entity.WarMode;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarAttackSlotUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarAttackTeamUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarAttackTeamsUpdateRequestDto;
import com.gameplatform.playerprofileservice.repository.PlayerProfileHeroRepository;
import com.gameplatform.playerprofileservice.repository.TeamWarAttackHeroRepository;
import com.gameplatform.playerprofileservice.repository.TeamWarAttackRepository;
import com.gameplatform.playerprofileservice.repository.WarModeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerWarAttackTeamService {

    private static final int TEAM_COUNT = 6;
    private static final int TEAM_SIZE = 5;

    private final PlayerProfileService playerProfileService;
    private final PlayerProfileHeroRepository playerProfileHeroRepository;
    private final TeamWarAttackRepository teamWarAttackRepository;
    private final TeamWarAttackHeroRepository teamWarAttackHeroRepository;
    private final WarModeRepository warModeRepository;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    @Transactional
    public WarAttackTeamsView getMyTeams(UUID userId, String email) {
        PlayerProfile playerProfile = playerProfileService.getOrCreateProfile(userId, email);
        List<WarMode> warModes = getActiveWarModes();
        List<TeamWarAttack> teams = getOrCreateTeams(playerProfile.getId(), warModes);
        return buildTeamsView(warModes, teams);
    }

    @Transactional
    public WarAttackTeamsView updateMyTeams(UUID userId,
                                            String email,
                                            PlayerWarAttackTeamsUpdateRequestDto request) {
        PlayerProfile playerProfile = playerProfileService.getOrCreateProfile(userId, email);
        List<WarMode> warModes = getActiveWarModes();
        Map<String, WarMode> warModeByCode = warModes.stream()
                .collect(Collectors.toMap(mode -> mode.getCode().toUpperCase(), Function.identity()));
        validateRequest(request, warModeByCode.keySet());

        List<TeamWarAttack> teams = getOrCreateTeams(playerProfile.getId(), warModes);
        Map<String, TeamWarAttack> teamByModeAndIndex = teams.stream()
                .collect(Collectors.toMap(
                        team -> buildTeamKey(resolveWarModeCode(team.getWarModeId(), warModes), (int) team.getTeamIndex()),
                        Function.identity()
                ));

        Set<UUID> requestedProfileHeroIds = request.teams().stream()
                .flatMap(team -> team.slots().stream())
                .map(PlayerWarAttackSlotUpdateRequestDto::playerProfileHeroId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        validateProfileHeroOwnership(playerProfile.getId(), requestedProfileHeroIds);

        OffsetDateTime now = OffsetDateTime.now(clock);
        Collection<UUID> teamIds = teams.stream()
                .map(TeamWarAttack::getId)
                .toList();

        teamWarAttackHeroRepository.deleteAllByTeamWarAttackIdIn(teamIds);
        teamWarAttackHeroRepository.flush();

        List<TeamWarAttackHero> heroesToSave = new ArrayList<>();
        for (PlayerWarAttackTeamUpdateRequestDto requestTeam : request.teams()) {
            TeamWarAttack team = teamByModeAndIndex.get(buildTeamKey(requestTeam.warModeCode(), requestTeam.teamIndex()));
            team.setUpdatedAt(now);

            for (PlayerWarAttackSlotUpdateRequestDto requestSlot : requestTeam.slots()) {
                if (requestSlot.playerProfileHeroId() == null) {
                    continue;
                }

                heroesToSave.add(TeamWarAttackHero.builder()
                        .id(UUID.randomUUID())
                        .teamWarAttackId(team.getId())
                        .playerProfileHeroId(requestSlot.playerProfileHeroId())
                        .slot(requestSlot.slot().shortValue())
                        .createdAt(now)
                        .build());
            }
        }

        teamWarAttackRepository.saveAll(teams);
        if (!heroesToSave.isEmpty()) {
            teamWarAttackHeroRepository.saveAll(heroesToSave);
        }

        return buildTeamsView(warModes, teams, heroesToSave);
    }

    protected List<TeamWarAttack> getOrCreateTeams(UUID playerProfileId, List<WarMode> warModes) {
        int expectedTeamCount = TEAM_COUNT * warModes.size();
        List<TeamWarAttack> existingTeams = teamWarAttackRepository.findAllByPlayerProfileId(playerProfileId);
        if (existingTeams.size() == expectedTeamCount) {
            return existingTeams;
        }

        OffsetDateTime now = OffsetDateTime.now(clock);
        Set<String> existingKeys = existingTeams.stream()
                .map(team -> buildTeamKey(team.getWarModeId(), team.getTeamIndex()))
                .collect(Collectors.toSet());

        List<TeamWarAttack> teamsToCreate = new ArrayList<>();
        for (WarMode warMode : warModes) {
            for (short teamIndex = 1; teamIndex <= TEAM_COUNT; teamIndex++) {
                if (existingKeys.contains(buildTeamKey(warMode.getId(), teamIndex))) {
                    continue;
                }

                teamsToCreate.add(TeamWarAttack.builder()
                        .id(UUID.randomUUID())
                        .playerProfileId(playerProfileId)
                        .warModeId(warMode.getId())
                        .teamIndex(teamIndex)
                        .createdAt(now)
                        .updatedAt(now)
                        .build());
            }
        }

        if (!teamsToCreate.isEmpty()) {
            teamWarAttackRepository.saveAll(teamsToCreate);
            existingTeams = teamWarAttackRepository.findAllByPlayerProfileId(playerProfileId);
        }

        return existingTeams;
    }

    private WarAttackTeamsView buildTeamsView(List<WarMode> warModes, List<TeamWarAttack> teams) {
        List<UUID> teamIds = teams.stream()
                .map(TeamWarAttack::getId)
                .toList();

        List<TeamWarAttackHero> teamHeroes = teamIds.isEmpty()
                ? List.of()
                : teamWarAttackHeroRepository.findAllByTeamWarAttackIdIn(teamIds);

        return buildTeamsView(warModes, teams, teamHeroes);
    }

    private WarAttackTeamsView buildTeamsView(List<WarMode> warModes,
                                              List<TeamWarAttack> teams,
                                              List<TeamWarAttackHero> teamHeroes) {
        Map<UUID, Map<Short, UUID>> heroByTeamAndSlot = new HashMap<>();
        Map<UUID, WarMode> warModeById = warModes.stream()
                .collect(Collectors.toMap(WarMode::getId, Function.identity()));

        for (TeamWarAttackHero teamHero : teamHeroes) {
            heroByTeamAndSlot
                    .computeIfAbsent(teamHero.getTeamWarAttackId(), ignored -> new HashMap<>())
                    .put(teamHero.getSlot(), teamHero.getPlayerProfileHeroId());
        }

        List<WarModeView> warModeViews = warModes.stream()
                .map(this::toWarModeView)
                .toList();

        List<WarAttackTeamView> teamViews = teams.stream()
                .sorted(Comparator
                        .comparing((TeamWarAttack team) -> warModeById.get(team.getWarModeId()).getSortOrder())
                        .thenComparing(TeamWarAttack::getTeamIndex))
                .map(team -> {
                    Map<Short, UUID> slotMap = heroByTeamAndSlot.getOrDefault(team.getId(), Map.of());
                    List<WarAttackSlotView> slots = new ArrayList<>(TEAM_SIZE);
                    for (short slot = 1; slot <= TEAM_SIZE; slot++) {
                        slots.add(WarAttackSlotView.builder()
                                .slot((int) slot)
                                .playerProfileHeroId(slotMap.get(slot))
                                .build());
                    }

                    return WarAttackTeamView.builder()
                            .id(team.getId())
                            .warModeCode(warModeById.get(team.getWarModeId()).getCode())
                            .teamIndex((int) team.getTeamIndex())
                            .slots(slots)
                            .build();
                })
                .toList();

        return WarAttackTeamsView.builder()
                .warModes(warModeViews)
                .teams(teamViews)
                .build();
    }

    private void validateRequest(PlayerWarAttackTeamsUpdateRequestDto request, Set<String> allowedWarModeCodes) {
        Map<String, Set<Integer>> teamIndexesByMode = new HashMap<>();
        Map<String, Set<UUID>> usedProfileHeroIdsByMode = new HashMap<>();

        for (PlayerWarAttackTeamUpdateRequestDto team : request.teams()) {
            String warModeCode = normalizeModeCode(team.warModeCode());
            if (!allowedWarModeCodes.contains(warModeCode)) {
                throw new ResponseStatusException(BAD_REQUEST, "Unknown war mode code: " + team.warModeCode());
            }

            if (!teamIndexesByMode.computeIfAbsent(warModeCode, ignored -> new HashSet<>()).add(team.teamIndex())) {
                throw new ResponseStatusException(BAD_REQUEST, "Duplicate team index for war mode " + warModeCode + ": " + team.teamIndex());
            }

            Set<Integer> slots = new HashSet<>();
            for (PlayerWarAttackSlotUpdateRequestDto slot : team.slots()) {
                if (!slots.add(slot.slot())) {
                    throw new ResponseStatusException(
                            BAD_REQUEST,
                            "Duplicate slot " + slot.slot() + " in team " + team.teamIndex()
                    );
                }

                UUID playerProfileHeroId = slot.playerProfileHeroId();
                if (playerProfileHeroId != null
                        && !usedProfileHeroIdsByMode.computeIfAbsent(warModeCode, ignored -> new HashSet<>()).add(playerProfileHeroId)) {
                    throw new ResponseStatusException(
                            BAD_REQUEST,
                            "Profile hero is already used in another war team for mode " + warModeCode + ": " + playerProfileHeroId
                    );
                }
            }
        }

        for (String warModeCode : allowedWarModeCodes) {
            Set<Integer> modeTeamIndexes = teamIndexesByMode.getOrDefault(warModeCode, Set.of());
            for (int teamIndex = 1; teamIndex <= TEAM_COUNT; teamIndex++) {
                if (!modeTeamIndexes.contains(teamIndex)) {
                    throw new ResponseStatusException(BAD_REQUEST, "Missing team index " + teamIndex + " for war mode " + warModeCode);
                }
            }
        }
    }

    private List<WarMode> getActiveWarModes() {
        List<WarMode> warModes = warModeRepository.findAllByActiveTrueOrderBySortOrderAsc();
        if (warModes.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "No active war modes configured");
        }
        return warModes;
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

    private String resolveWarModeCode(UUID warModeId, List<WarMode> warModes) {
        return warModes.stream()
                .filter(warMode -> warMode.getId().equals(warModeId))
                .map(WarMode::getCode)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Unknown war mode id: " + warModeId));
    }

    private String normalizeModeCode(String warModeCode) {
        return warModeCode.trim().toUpperCase();
    }

    private String buildTeamKey(String warModeCode, int teamIndex) {
        return normalizeModeCode(warModeCode) + ":" + teamIndex;
    }

    private String buildTeamKey(UUID warModeId, short teamIndex) {
        return warModeId + ":" + teamIndex;
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

    @Builder
    public record WarAttackSlotView(
            Integer slot,
            UUID playerProfileHeroId
    ) {
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
    public record WarAttackTeamView(
            UUID id,
            String warModeCode,
            Integer teamIndex,
            List<WarAttackSlotView> slots
    ) {
    }

    @Builder
    public record WarAttackTeamsView(
            List<WarModeView> warModes,
            List<WarAttackTeamView> teams
    ) {
    }
}
