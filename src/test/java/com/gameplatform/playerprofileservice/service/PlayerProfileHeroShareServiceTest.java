package com.gameplatform.playerprofileservice.service;

import com.gameplatform.playerprofileservice.domain.entity.PlayerProfile;
import com.gameplatform.playerprofileservice.domain.entity.PlayerProfileHero;
import com.gameplatform.playerprofileservice.domain.entity.PlayerProfileHeroShare;
import com.gameplatform.playerprofileservice.domain.entity.PlayerProfileHeroShareItem;
import com.gameplatform.playerprofileservice.domain.enums.HeroPowerGrade;
import com.gameplatform.playerprofileservice.dto.response.PlayerProfileHeroShareResponseDto;
import com.gameplatform.playerprofileservice.repository.PlayerProfileHeroRepository;
import com.gameplatform.playerprofileservice.repository.PlayerProfileHeroShareItemRepository;
import com.gameplatform.playerprofileservice.repository.PlayerProfileHeroShareRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerProfileHeroShareServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-14T12:00:00Z");

    @Mock
    private PlayerProfileService playerProfileService;

    @Mock
    private PlayerProfileHeroRepository playerProfileHeroRepository;

    @Mock
    private PlayerProfileHeroShareRepository playerProfileHeroShareRepository;

    @Mock
    private PlayerProfileHeroShareItemRepository playerProfileHeroShareItemRepository;

    private PlayerProfileHeroShareService service;

    @BeforeEach
    void setUp() {
        service = new PlayerProfileHeroShareService(
                playerProfileService,
                playerProfileHeroRepository,
                playerProfileHeroShareRepository,
                playerProfileHeroShareItemRepository,
                Clock.fixed(NOW, ZoneOffset.UTC)
        );
    }

    @Test
    void shouldCreateOrderedThreeDaySnapshotFromOwnedHeroes() {
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();
        PlayerProfile profile = PlayerProfile.builder().id(profileId).build();
        PlayerProfileHero first = hero(firstId, profileId, 101L, HeroPowerGrade.FIRST_TIER, 5, 0);
        PlayerProfileHero second = hero(secondId, profileId, 202L, HeroPowerGrade.FIRST_LIMIT_BROKEN, 17, 3);

        when(playerProfileService.getRequiredProfile(userId, "user@example.com")).thenReturn(profile);
        when(playerProfileHeroRepository.findAllByIdInAndPlayerProfileId(List.of(secondId, firstId), profileId))
                .thenReturn(List.of(first, second));
        when(playerProfileHeroShareRepository.save(any(PlayerProfileHeroShare.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(playerProfileHeroShareItemRepository.saveAll(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PlayerProfileHeroShareResponseDto result = service.createShare(
                userId,
                "user@example.com",
                List.of(secondId, firstId),
                3
        );

        assertEquals(OffsetDateTime.parse("2026-11-17T12:00:00Z"), result.expiresAt());
        assertEquals(List.of(202L, 101L), result.heroes().stream().map(item -> item.heroId()).toList());
        assertEquals(17, result.heroes().get(0).talentLevel());
        assertEquals(3, result.heroes().get(0).costumeCount());
    }

    @Test
    void shouldCreateShareWithoutExpiration() {
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UUID heroId = UUID.randomUUID();

        when(playerProfileService.getRequiredProfile(userId, "user@example.com"))
                .thenReturn(PlayerProfile.builder().id(profileId).build());
        when(playerProfileHeroRepository.findAllByIdInAndPlayerProfileId(List.of(heroId), profileId))
                .thenReturn(List.of(hero(heroId, profileId, 101L, HeroPowerGrade.FULLY_ASCENDED, 0, 0)));
        when(playerProfileHeroShareRepository.save(any(PlayerProfileHeroShare.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(playerProfileHeroShareItemRepository.saveAll(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PlayerProfileHeroShareResponseDto result = service.createShare(
                userId,
                "user@example.com",
                List.of(heroId),
                null
        );

        assertNull(result.expiresAt());
    }

    @Test
    void shouldReturnGoneWhenShareExpired() {
        UUID token = UUID.randomUUID();
        PlayerProfileHeroShare expired = PlayerProfileHeroShare.builder()
                .id(UUID.randomUUID())
                .token(token)
                .createdAt(OffsetDateTime.parse("2026-11-10T12:00:00Z"))
                .expiresAt(OffsetDateTime.parse("2026-11-14T12:00:00Z"))
                .build();
        when(playerProfileHeroShareRepository.findByToken(token)).thenReturn(Optional.of(expired));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.getPublicShare(token)
        );

        assertEquals(HttpStatus.GONE, exception.getStatusCode());
    }

    @Test
    void shouldRejectHeroThatDoesNotBelongToProfile() {
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UUID requestedHeroId = UUID.randomUUID();

        when(playerProfileService.getRequiredProfile(userId, "user@example.com"))
                .thenReturn(PlayerProfile.builder().id(profileId).build());
        when(playerProfileHeroRepository.findAllByIdInAndPlayerProfileId(List.of(requestedHeroId), profileId))
                .thenReturn(List.of());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.createShare(userId, "user@example.com", List.of(requestedHeroId), 3)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    private PlayerProfileHero hero(
            UUID id,
            UUID profileId,
            Long heroId,
            HeroPowerGrade powerGrade,
            int talentLevel,
            int costumeCount
    ) {
        return PlayerProfileHero.builder()
                .id(id)
                .playerProfileId(profileId)
                .heroId(heroId)
                .powerGrade(powerGrade)
                .talentLevel(talentLevel)
                .costumeCount(costumeCount)
                .createdAt(OffsetDateTime.now(Clock.fixed(NOW, ZoneOffset.UTC)))
                .build();
    }
}
