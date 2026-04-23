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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerWarAttackTeamServiceTest {

    @Mock
    private PlayerProfileService playerProfileService;

    @Mock
    private PlayerProfileHeroRepository playerProfileHeroRepository;

    @Mock
    private TeamWarAttackRepository teamWarAttackRepository;

    @Mock
    private TeamWarAttackHeroRepository teamWarAttackHeroRepository;

    private PlayerWarAttackTeamService playerWarAttackTeamService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-04-23T12:00:00Z"), ZoneOffset.UTC);
        playerWarAttackTeamService = new PlayerWarAttackTeamService(
                playerProfileService,
                playerProfileHeroRepository,
                teamWarAttackRepository,
                teamWarAttackHeroRepository,
                clock
        );
    }

    @Test
    void shouldReturnSixTeamsWithFiveSlots() {
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        List<TeamWarAttack> teams = buildTeams(profileId);
        List<TeamWarAttackHero> teamHeroes = List.of(
                buildTeamHero(teams.get(0).getId(), UUID.randomUUID(), (short) 1),
                buildTeamHero(teams.get(0).getId(), UUID.randomUUID(), (short) 3),
                buildTeamHero(teams.get(1).getId(), UUID.randomUUID(), (short) 2)
        );

        when(playerProfileService.getOrCreateProfile(userId, "user@example.com")).thenReturn(buildProfile(profileId, userId));
        when(teamWarAttackRepository.findAllByPlayerProfileIdOrderByTeamIndexAsc(profileId)).thenReturn(teams);
        when(teamWarAttackHeroRepository.findAllByTeamWarAttackIdIn(teams.stream().map(TeamWarAttack::getId).toList()))
                .thenReturn(teamHeroes);

        List<PlayerWarAttackTeamService.WarAttackTeamView> result = playerWarAttackTeamService.getMyTeams(userId, "user@example.com");

        assertEquals(6, result.size());
        assertEquals(5, result.get(0).slots().size());
        assertEquals(1, result.get(0).teamIndex());
        assertNull(result.get(0).slots().get(1).playerProfileHeroId());
    }

    @Test
    void shouldRejectDuplicateProfileHeroUsageAcrossTeams() {
        UUID duplicateHeroId = UUID.randomUUID();

        PlayerWarAttackTeamsUpdateRequestDto request = new PlayerWarAttackTeamsUpdateRequestDto(List.of(
                buildTeamRequest(1, duplicateHeroId, null, null, null, null),
                buildTeamRequest(2, duplicateHeroId, null, null, null, null),
                buildTeamRequest(3, null, null, null, null, null),
                buildTeamRequest(4, null, null, null, null, null),
                buildTeamRequest(5, null, null, null, null, null),
                buildTeamRequest(6, null, null, null, null, null)
        ));

        assertThrows(ResponseStatusException.class, () ->
                playerWarAttackTeamService.updateMyTeams(UUID.randomUUID(), "user@example.com", request)
        );
    }

    @Test
    void shouldRejectForeignProfileHero() {
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UUID foreignProfileId = UUID.randomUUID();
        UUID profileHeroId = UUID.randomUUID();
        List<TeamWarAttack> teams = buildTeams(profileId);

        when(playerProfileService.getOrCreateProfile(userId, "user@example.com")).thenReturn(buildProfile(profileId, userId));
        when(teamWarAttackRepository.findAllByPlayerProfileIdOrderByTeamIndexAsc(profileId)).thenReturn(teams);
        when(playerProfileHeroRepository.findAllById(any())).thenReturn(List.of(PlayerProfileHero.builder()
                .id(profileHeroId)
                .playerProfileId(foreignProfileId)
                .heroId(101L)
                .createdAt(OffsetDateTime.parse("2026-04-23T12:00:00Z"))
                .build()));

        PlayerWarAttackTeamsUpdateRequestDto request = new PlayerWarAttackTeamsUpdateRequestDto(List.of(
                buildTeamRequest(1, profileHeroId, null, null, null, null),
                buildTeamRequest(2, null, null, null, null, null),
                buildTeamRequest(3, null, null, null, null, null),
                buildTeamRequest(4, null, null, null, null, null),
                buildTeamRequest(5, null, null, null, null, null),
                buildTeamRequest(6, null, null, null, null, null)
        ));

        assertThrows(ResponseStatusException.class, () ->
                playerWarAttackTeamService.updateMyTeams(userId, "user@example.com", request)
        );

        verify(teamWarAttackHeroRepository, never()).deleteAllByTeamWarAttackIdIn(anyList());
    }

    @Test
    void shouldReplaceTeamAssignments() {
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UUID profileHeroId = UUID.randomUUID();
        List<TeamWarAttack> teams = buildTeams(profileId);

        when(playerProfileService.getOrCreateProfile(userId, "user@example.com")).thenReturn(buildProfile(profileId, userId));
        when(teamWarAttackRepository.findAllByPlayerProfileIdOrderByTeamIndexAsc(profileId)).thenReturn(teams);
        when(playerProfileHeroRepository.findAllById(any())).thenReturn(List.of(PlayerProfileHero.builder()
                .id(profileHeroId)
                .playerProfileId(profileId)
                .heroId(101L)
                .createdAt(OffsetDateTime.parse("2026-04-23T12:00:00Z"))
                .build()));
        when(teamWarAttackHeroRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        PlayerWarAttackTeamsUpdateRequestDto request = new PlayerWarAttackTeamsUpdateRequestDto(List.of(
                buildTeamRequest(1, profileHeroId, null, null, null, null),
                buildTeamRequest(2, null, null, null, null, null),
                buildTeamRequest(3, null, null, null, null, null),
                buildTeamRequest(4, null, null, null, null, null),
                buildTeamRequest(5, null, null, null, null, null),
                buildTeamRequest(6, null, null, null, null, null)
        ));

        List<PlayerWarAttackTeamService.WarAttackTeamView> result =
                playerWarAttackTeamService.updateMyTeams(userId, "user@example.com", request);

        assertEquals(profileHeroId, result.get(0).slots().get(0).playerProfileHeroId());
        verify(teamWarAttackHeroRepository).deleteAllByTeamWarAttackIdIn(teams.stream().map(TeamWarAttack::getId).toList());
        verify(teamWarAttackHeroRepository).saveAll(anyList());
    }

    private PlayerProfile buildProfile(UUID profileId, UUID userId) {
        return PlayerProfile.builder()
                .id(profileId)
                .userId(userId)
                .email("user@example.com")
                .createdAt(OffsetDateTime.parse("2026-04-22T12:00:00Z"))
                .updatedAt(OffsetDateTime.parse("2026-04-22T12:00:00Z"))
                .build();
    }

    private List<TeamWarAttack> buildTeams(UUID profileId) {
        return List.of(
                buildTeam(profileId, (short) 1),
                buildTeam(profileId, (short) 2),
                buildTeam(profileId, (short) 3),
                buildTeam(profileId, (short) 4),
                buildTeam(profileId, (short) 5),
                buildTeam(profileId, (short) 6)
        );
    }

    private TeamWarAttack buildTeam(UUID profileId, short teamIndex) {
        return TeamWarAttack.builder()
                .id(UUID.randomUUID())
                .playerProfileId(profileId)
                .teamIndex(teamIndex)
                .createdAt(OffsetDateTime.parse("2026-04-22T12:00:00Z"))
                .updatedAt(OffsetDateTime.parse("2026-04-22T12:00:00Z"))
                .build();
    }

    private TeamWarAttackHero buildTeamHero(UUID teamId, UUID profileHeroId, short slot) {
        return TeamWarAttackHero.builder()
                .id(UUID.randomUUID())
                .teamWarAttackId(teamId)
                .playerProfileHeroId(profileHeroId)
                .slot(slot)
                .createdAt(OffsetDateTime.parse("2026-04-23T12:00:00Z"))
                .build();
    }

    private PlayerWarAttackTeamUpdateRequestDto buildTeamRequest(int teamIndex,
                                                                 UUID slot1,
                                                                 UUID slot2,
                                                                 UUID slot3,
                                                                 UUID slot4,
                                                                 UUID slot5) {
        return new PlayerWarAttackTeamUpdateRequestDto(
                teamIndex,
                List.of(
                        new PlayerWarAttackSlotUpdateRequestDto(1, slot1),
                        new PlayerWarAttackSlotUpdateRequestDto(2, slot2),
                        new PlayerWarAttackSlotUpdateRequestDto(3, slot3),
                        new PlayerWarAttackSlotUpdateRequestDto(4, slot4),
                        new PlayerWarAttackSlotUpdateRequestDto(5, slot5)
                )
        );
    }
}
