package com.gameplatform.playerprofileservice.service;

import com.gameplatform.playerprofileservice.domain.entity.PlayerProfile;
import com.gameplatform.playerprofileservice.domain.entity.PlayerProfileHero;
import com.gameplatform.playerprofileservice.domain.entity.TeamWarAttack;
import com.gameplatform.playerprofileservice.domain.entity.TeamWarAttackHero;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarAttackSlotUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarAttackTeamUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarAttackTeamsUpdateRequestDto;
import com.gameplatform.playerprofileservice.repository.PlayerProfileHeroRepository;
import com.gameplatform.playerprofileservice.repository.TeamWarAttackHeroRepository;
import com.gameplatform.playerprofileservice.repository.TeamWarAttackRepository;
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
    private final Clock clock;

    @Transactional
    public List<WarAttackTeamView> getMyTeams(UUID userId, String email) {
        PlayerProfile playerProfile = playerProfileService.getOrCreateProfile(userId, email);
        List<TeamWarAttack> teams = getOrCreateTeams(playerProfile.getId());
        return buildTeamViews(teams);
    }

    @Transactional
    public List<WarAttackTeamView> updateMyTeams(UUID userId,
                                                 String email,
                                                 PlayerWarAttackTeamsUpdateRequestDto request) {
        PlayerProfile playerProfile = playerProfileService.getOrCreateProfile(userId, email);
        validateRequest(request);

        List<TeamWarAttack> teams = getOrCreateTeams(playerProfile.getId());
        Map<Integer, TeamWarAttack> teamByIndex = teams.stream()
                .collect(Collectors.toMap(team -> (int) team.getTeamIndex(), Function.identity()));

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
            TeamWarAttack team = teamByIndex.get(requestTeam.teamIndex());
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

        return buildTeamViews(teams, heroesToSave);
    }

    protected List<TeamWarAttack> getOrCreateTeams(UUID playerProfileId) {
        List<TeamWarAttack> existingTeams = teamWarAttackRepository.findAllByPlayerProfileIdOrderByTeamIndexAsc(playerProfileId);
        if (existingTeams.size() == TEAM_COUNT) {
            return existingTeams;
        }

        OffsetDateTime now = OffsetDateTime.now(clock);
        Set<Short> existingIndexes = existingTeams.stream()
                .map(TeamWarAttack::getTeamIndex)
                .collect(Collectors.toSet());

        List<TeamWarAttack> teamsToCreate = new ArrayList<>();
        for (short teamIndex = 1; teamIndex <= TEAM_COUNT; teamIndex++) {
            if (existingIndexes.contains(teamIndex)) {
                continue;
            }

            teamsToCreate.add(TeamWarAttack.builder()
                    .id(UUID.randomUUID())
                    .playerProfileId(playerProfileId)
                    .teamIndex(teamIndex)
                    .createdAt(now)
                    .updatedAt(now)
                    .build());
        }

        if (!teamsToCreate.isEmpty()) {
            teamWarAttackRepository.saveAll(teamsToCreate);
            existingTeams = teamWarAttackRepository.findAllByPlayerProfileIdOrderByTeamIndexAsc(playerProfileId);
        }

        return existingTeams;
    }

    private List<WarAttackTeamView> buildTeamViews(List<TeamWarAttack> teams) {
        List<UUID> teamIds = teams.stream()
                .map(TeamWarAttack::getId)
                .toList();

        List<TeamWarAttackHero> teamHeroes = teamIds.isEmpty()
                ? List.of()
                : teamWarAttackHeroRepository.findAllByTeamWarAttackIdIn(teamIds);

        return buildTeamViews(teams, teamHeroes);
    }

    private List<WarAttackTeamView> buildTeamViews(List<TeamWarAttack> teams,
                                                   List<TeamWarAttackHero> teamHeroes) {
        Map<UUID, Map<Short, UUID>> heroByTeamAndSlot = new HashMap<>();

        for (TeamWarAttackHero teamHero : teamHeroes) {
            heroByTeamAndSlot
                    .computeIfAbsent(teamHero.getTeamWarAttackId(), ignored -> new HashMap<>())
                    .put(teamHero.getSlot(), teamHero.getPlayerProfileHeroId());
        }

        return teams.stream()
                .sorted(Comparator.comparing(TeamWarAttack::getTeamIndex))
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
                            .teamIndex((int) team.getTeamIndex())
                            .slots(slots)
                            .build();
                })
                .toList();
    }

    private void validateRequest(PlayerWarAttackTeamsUpdateRequestDto request) {
        Set<Integer> teamIndexes = new HashSet<>();
        Set<UUID> usedProfileHeroIds = new HashSet<>();

        for (PlayerWarAttackTeamUpdateRequestDto team : request.teams()) {
            if (!teamIndexes.add(team.teamIndex())) {
                throw new ResponseStatusException(BAD_REQUEST, "Duplicate team index: " + team.teamIndex());
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
                if (playerProfileHeroId != null && !usedProfileHeroIds.add(playerProfileHeroId)) {
                    throw new ResponseStatusException(
                            BAD_REQUEST,
                            "Profile hero is already used in another war team: " + playerProfileHeroId
                    );
                }
            }
        }

        for (int teamIndex = 1; teamIndex <= TEAM_COUNT; teamIndex++) {
            if (!teamIndexes.contains(teamIndex)) {
                throw new ResponseStatusException(BAD_REQUEST, "Missing team index: " + teamIndex);
            }
        }
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
    public record WarAttackTeamView(
            UUID id,
            Integer teamIndex,
            List<WarAttackSlotView> slots
    ) {
    }
}
