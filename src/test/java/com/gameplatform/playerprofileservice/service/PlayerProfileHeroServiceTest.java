package com.gameplatform.playerprofileservice.service;

import com.gameplatform.playerprofileservice.domain.entity.PlayerProfile;
import com.gameplatform.playerprofileservice.domain.entity.PlayerProfileHero;
import com.gameplatform.playerprofileservice.domain.enums.HeroPowerGrade;
import com.gameplatform.playerprofileservice.repository.PlayerProfileHeroRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerProfileHeroServiceTest {

    @Mock
    private PlayerProfileService playerProfileService;

    @Mock
    private PlayerProfileHeroRepository playerProfileHeroRepository;

    @InjectMocks
    private PlayerProfileHeroService playerProfileHeroService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-04-21T12:00:00Z"), ZoneOffset.UTC);
        playerProfileHeroService = new PlayerProfileHeroService(
                playerProfileService,
                playerProfileHeroRepository,
                clock
        );
    }

    @Test
    void shouldReturnAllHeroesForProfile() {
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        PlayerProfile playerProfile = buildProfile(profileId, userId);

        List<PlayerProfileHero> heroes = List.of(
                buildProfileHero(profileId, 101L),
                buildProfileHero(profileId, 202L)
        );

        when(playerProfileService.getOrCreateProfile(userId, "user@example.com")).thenReturn(playerProfile);
        when(playerProfileHeroRepository.findAllByPlayerProfileIdOrderByCreatedAtAsc(profileId)).thenReturn(heroes);

        List<PlayerProfileHero> result = playerProfileHeroService.getMyHeroes(userId, "user@example.com");

        assertEquals(2, result.size());
        assertEquals(101L, result.get(0).getHeroId());
        assertEquals(202L, result.get(1).getHeroId());
    }

    @Test
    void shouldAllowAddingDuplicateHeroIds() {
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        PlayerProfile playerProfile = buildProfile(profileId, userId);

        when(playerProfileService.getOrCreateProfile(userId, "user@example.com")).thenReturn(playerProfile);
        when(playerProfileHeroRepository.save(any(PlayerProfileHero.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlayerProfileHero firstResult = playerProfileHeroService.addHero(userId, "user@example.com", 101L);
        PlayerProfileHero secondResult = playerProfileHeroService.addHero(userId, "user@example.com", 101L);

        assertEquals(101L, firstResult.getHeroId());
        assertEquals(101L, secondResult.getHeroId());
        assertEquals(profileId, firstResult.getPlayerProfileId());
        assertEquals(profileId, secondResult.getPlayerProfileId());
        assertEquals(HeroPowerGrade.FULLY_ASCENDED, firstResult.getPowerGrade());
        assertEquals(HeroPowerGrade.FULLY_ASCENDED, secondResult.getPowerGrade());
        assertEquals(0, firstResult.getTalentLevel());
        assertEquals(0, secondResult.getTalentLevel());
    }

    @Test
    void shouldUpdateHeroPowerGrade() {
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UUID profileHeroId = UUID.randomUUID();

        PlayerProfile playerProfile = buildProfile(profileId, userId);
        PlayerProfileHero playerProfileHero = PlayerProfileHero.builder()
                .id(profileHeroId)
                .playerProfileId(profileId)
                .heroId(101L)
                .powerGrade(HeroPowerGrade.FULLY_ASCENDED)
                .createdAt(OffsetDateTime.parse("2026-04-21T12:00:00Z"))
                .build();

        when(playerProfileService.getOrCreateProfile(userId, "user@example.com")).thenReturn(playerProfile);
        when(playerProfileHeroRepository.findByIdAndPlayerProfileId(profileHeroId, profileId))
                .thenReturn(Optional.of(playerProfileHero));
        when(playerProfileHeroRepository.save(any(PlayerProfileHero.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlayerProfileHero updated = playerProfileHeroService.updateHeroPowerGrade(
                userId,
                "user@example.com",
                profileHeroId,
                HeroPowerGrade.FIRST_LIMIT_BROKEN
        );

        assertEquals(HeroPowerGrade.FIRST_LIMIT_BROKEN, updated.getPowerGrade());
        verify(playerProfileHeroRepository).save(playerProfileHero);
    }

    @Test
    void shouldUpdateHeroTalentLevel() {
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UUID profileHeroId = UUID.randomUUID();

        PlayerProfile playerProfile = buildProfile(profileId, userId);
        PlayerProfileHero playerProfileHero = PlayerProfileHero.builder()
                .id(profileHeroId)
                .playerProfileId(profileId)
                .heroId(101L)
                .powerGrade(HeroPowerGrade.FULLY_ASCENDED)
                .talentLevel(0)
                .createdAt(OffsetDateTime.parse("2026-04-21T12:00:00Z"))
                .build();

        when(playerProfileService.getOrCreateProfile(userId, "user@example.com")).thenReturn(playerProfile);
        when(playerProfileHeroRepository.findByIdAndPlayerProfileId(profileHeroId, profileId))
                .thenReturn(Optional.of(playerProfileHero));
        when(playerProfileHeroRepository.save(any(PlayerProfileHero.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlayerProfileHero updated = playerProfileHeroService.updateHeroTalentLevel(
                userId,
                "user@example.com",
                profileHeroId,
                25
        );

        assertEquals(25, updated.getTalentLevel());
        verify(playerProfileHeroRepository).save(playerProfileHero);
    }

    @Test
    void shouldDeleteHeroByProfileHeroId() {
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UUID profileHeroId = UUID.randomUUID();

        PlayerProfile playerProfile = buildProfile(profileId, userId);
        PlayerProfileHero playerProfileHero = PlayerProfileHero.builder()
                .id(profileHeroId)
                .playerProfileId(profileId)
                .heroId(101L)
                .powerGrade(HeroPowerGrade.FULLY_ASCENDED)
                .talentLevel(0)
                .createdAt(OffsetDateTime.parse("2026-04-21T12:00:00Z"))
                .build();

        when(playerProfileService.getOrCreateProfile(userId, "user@example.com")).thenReturn(playerProfile);
        when(playerProfileHeroRepository.findByIdAndPlayerProfileId(profileHeroId, profileId))
                .thenReturn(Optional.of(playerProfileHero));

        assertDoesNotThrow(() -> playerProfileHeroService.deleteHero(userId, "user@example.com", profileHeroId));

        verify(playerProfileHeroRepository).delete(playerProfileHero);
    }

    @Test
    void shouldThrowWhenProfileHeroNotFoundForDelete() {
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UUID profileHeroId = UUID.randomUUID();

        PlayerProfile playerProfile = buildProfile(profileId, userId);

        when(playerProfileService.getOrCreateProfile(userId, "user@example.com")).thenReturn(playerProfile);
        when(playerProfileHeroRepository.findByIdAndPlayerProfileId(profileHeroId, profileId))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> playerProfileHeroService.deleteHero(userId, "user@example.com", profileHeroId)
        );
    }

    private PlayerProfile buildProfile(UUID profileId, UUID userId) {
        return PlayerProfile.builder()
                .id(profileId)
                .userId(userId)
                .email("user@example.com")
                .createdAt(OffsetDateTime.parse("2026-04-20T12:00:00Z"))
                .updatedAt(OffsetDateTime.parse("2026-04-20T12:00:00Z"))
                .build();
    }

    private PlayerProfileHero buildProfileHero(UUID profileId, Long heroId) {
        return PlayerProfileHero.builder()
                .id(UUID.randomUUID())
                .playerProfileId(profileId)
                .heroId(heroId)
                .powerGrade(HeroPowerGrade.FULLY_ASCENDED)
                .talentLevel(0)
                .createdAt(OffsetDateTime.parse("2026-04-21T12:00:00Z"))
                .build();
    }
}
