package com.gameplatform.playerprofileservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameplatform.playerprofileservice.domain.entity.PlayerProfile;
import com.gameplatform.playerprofileservice.domain.entity.PlayerProfileHero;
import com.gameplatform.playerprofileservice.domain.entity.TeamWarAttack;
import com.gameplatform.playerprofileservice.domain.entity.TeamWarAttackHero;
import com.gameplatform.playerprofileservice.domain.entity.WarMode;
import com.gameplatform.playerprofileservice.domain.enums.HeroPowerGrade;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarAttackSlotUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarAttackTeamUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarAttackTeamsUpdateRequestDto;
import com.gameplatform.playerprofileservice.repository.PlayerProfileHeroRepository;
import com.gameplatform.playerprofileservice.repository.TeamWarAttackHeroRepository;
import com.gameplatform.playerprofileservice.repository.TeamWarAttackRepository;
import com.gameplatform.playerprofileservice.repository.WarModeRepository;
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

    @Mock
    private WarModeRepository warModeRepository;

    private PlayerWarAttackTeamService playerWarAttackTeamService;
    private WarMode universalWarMode;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-04-23T12:00:00Z"), ZoneOffset.UTC);
        universalWarMode = buildWarMode();
        playerWarAttackTeamService = new PlayerWarAttackTeamService(
                playerProfileService,
                playerProfileHeroRepository,
                teamWarAttackRepository,
                teamWarAttackHeroRepository,
                warModeRepository,
                new ObjectMapper(),
                clock
        );
        when(warModeRepository.findAllByActiveTrueOrderBySortOrderAsc()).thenReturn(List.of(universalWarMode));
    }

    @Test
    void shouldReturnSixTeamsWithFiveSlots() {
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        List<TeamWarAttack> teams = buildTeams(profileId, universalWarMode.getId());
        List<TeamWarAttackHero> teamHeroes = List.of(
                buildTeamHero(teams.get(0).getId(), UUID.randomUUID(), (short) 1),
                buildTeamHero(teams.get(0).getId(), UUID.randomUUID(), (short) 3),
                buildTeamHero(teams.get(1).getId(), UUID.randomUUID(), (short) 2)
        );

        when(playerProfileService.getOrCreateProfile(userId, "user@example.com")).thenReturn(buildProfile(profileId, userId));
        when(teamWarAttackRepository.findAllByPlayerProfileId(profileId)).thenReturn(teams);
        when(teamWarAttackHeroRepository.findAllByTeamWarAttackIdIn(teams.stream().map(TeamWarAttack::getId).toList()))
                .thenReturn(teamHeroes);

        PlayerWarAttackTeamService.WarAttackTeamsView result = playerWarAttackTeamService.getMyTeams(userId, "user@example.com");

        assertEquals(1, result.warModes().size());
        assertEquals(6, result.teams().size());
        assertEquals(5, result.teams().get(0).slots().size());
        assertEquals(1, result.teams().get(0).teamIndex());
        assertEquals("UNIVERSAL", result.teams().get(0).warModeCode());
        assertNull(result.teams().get(0).slots().get(1).playerProfileHeroId());
    }

    @Test
    void shouldRejectDuplicateProfileHeroUsageAcrossTeams() {
        UUID duplicateHeroId = UUID.randomUUID();

        PlayerWarAttackTeamsUpdateRequestDto request = new PlayerWarAttackTeamsUpdateRequestDto(List.of(
                buildTeamRequest("UNIVERSAL", 1, duplicateHeroId, null, null, null, null),
                buildTeamRequest("UNIVERSAL", 2, duplicateHeroId, null, null, null, null),
                buildTeamRequest("UNIVERSAL", 3, null, null, null, null, null),
                buildTeamRequest("UNIVERSAL", 4, null, null, null, null, null),
                buildTeamRequest("UNIVERSAL", 5, null, null, null, null, null),
                buildTeamRequest("UNIVERSAL", 6, null, null, null, null, null)
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
        List<TeamWarAttack> teams = buildTeams(profileId, universalWarMode.getId());

        when(playerProfileService.getOrCreateProfile(userId, "user@example.com")).thenReturn(buildProfile(profileId, userId));
        when(teamWarAttackRepository.findAllByPlayerProfileId(profileId)).thenReturn(teams);
        when(playerProfileHeroRepository.findAllById(any())).thenReturn(List.of(PlayerProfileHero.builder()
                .id(profileHeroId)
                .playerProfileId(foreignProfileId)
                .heroId(101L)
                .powerGrade(HeroPowerGrade.FULLY_ASCENDED)
                .createdAt(OffsetDateTime.parse("2026-04-23T12:00:00Z"))
                .build()));

        PlayerWarAttackTeamsUpdateRequestDto request = new PlayerWarAttackTeamsUpdateRequestDto(List.of(
                buildTeamRequest("UNIVERSAL", 1, profileHeroId, null, null, null, null),
                buildTeamRequest("UNIVERSAL", 2, null, null, null, null, null),
                buildTeamRequest("UNIVERSAL", 3, null, null, null, null, null),
                buildTeamRequest("UNIVERSAL", 4, null, null, null, null, null),
                buildTeamRequest("UNIVERSAL", 5, null, null, null, null, null),
                buildTeamRequest("UNIVERSAL", 6, null, null, null, null, null)
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
        List<TeamWarAttack> teams = buildTeams(profileId, universalWarMode.getId());

        when(playerProfileService.getOrCreateProfile(userId, "user@example.com")).thenReturn(buildProfile(profileId, userId));
        when(teamWarAttackRepository.findAllByPlayerProfileId(profileId)).thenReturn(teams);
        when(playerProfileHeroRepository.findAllById(any())).thenReturn(List.of(PlayerProfileHero.builder()
                .id(profileHeroId)
                .playerProfileId(profileId)
                .heroId(101L)
                .powerGrade(HeroPowerGrade.FULLY_ASCENDED)
                .createdAt(OffsetDateTime.parse("2026-04-23T12:00:00Z"))
                .build()));
        when(teamWarAttackHeroRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        PlayerWarAttackTeamsUpdateRequestDto request = new PlayerWarAttackTeamsUpdateRequestDto(List.of(
                buildTeamRequest("UNIVERSAL", 1, profileHeroId, null, null, null, null),
                buildTeamRequest("UNIVERSAL", 2, null, null, null, null, null),
                buildTeamRequest("UNIVERSAL", 3, null, null, null, null, null),
                buildTeamRequest("UNIVERSAL", 4, null, null, null, null, null),
                buildTeamRequest("UNIVERSAL", 5, null, null, null, null, null),
                buildTeamRequest("UNIVERSAL", 6, null, null, null, null, null)
        ));

        PlayerWarAttackTeamService.WarAttackTeamsView result =
                playerWarAttackTeamService.updateMyTeams(userId, "user@example.com", request);

        assertEquals(profileHeroId, result.teams().get(0).slots().get(0).playerProfileHeroId());
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

    private List<TeamWarAttack> buildTeams(UUID profileId, UUID warModeId) {
        return List.of(
                buildTeam(profileId, warModeId, (short) 1),
                buildTeam(profileId, warModeId, (short) 2),
                buildTeam(profileId, warModeId, (short) 3),
                buildTeam(profileId, warModeId, (short) 4),
                buildTeam(profileId, warModeId, (short) 5),
                buildTeam(profileId, warModeId, (short) 6)
        );
    }

    private TeamWarAttack buildTeam(UUID profileId, UUID warModeId, short teamIndex) {
        return TeamWarAttack.builder()
                .id(UUID.randomUUID())
                .playerProfileId(profileId)
                .warModeId(warModeId)
                .teamIndex(teamIndex)
                .createdAt(OffsetDateTime.parse("2026-04-22T12:00:00Z"))
                .updatedAt(OffsetDateTime.parse("2026-04-22T12:00:00Z"))
                .build();
    }

    private WarMode buildWarMode() {
        return WarMode.builder()
                .id(UUID.randomUUID())
                .code("UNIVERSAL")
                .nameJson("{\"ru\":\"Универсальная\",\"en\":\"Universal\"}")
                .descriptionJson("{\"ru\":\"Команды для любого режима войны\",\"en\":\"Teams for any war mode\"}")
                .sortOrder((short) 1)
                .active(true)
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

    private PlayerWarAttackTeamUpdateRequestDto buildTeamRequest(String warModeCode,
                                                                 int teamIndex,
                                                                 UUID slot1,
                                                                 UUID slot2,
                                                                 UUID slot3,
                                                                 UUID slot4,
                                                                 UUID slot5) {
        return new PlayerWarAttackTeamUpdateRequestDto(
                warModeCode,
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
