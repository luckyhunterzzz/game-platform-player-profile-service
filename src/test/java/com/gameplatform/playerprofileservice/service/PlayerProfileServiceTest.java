package com.gameplatform.playerprofileservice.service;

import com.gameplatform.playerprofileservice.domain.entity.PlayerProfile;
import com.gameplatform.playerprofileservice.domain.enums.PlayerProfileStatus;
import com.gameplatform.playerprofileservice.dto.request.PlayerProfileUpdateRequestDto;
import com.gameplatform.playerprofileservice.repository.PlayerProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerProfileServiceTest {

    @Mock
    private PlayerProfileRepository playerProfileRepository;

    @InjectMocks
    private PlayerProfileService playerProfileService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-04-19T12:00:00Z"), ZoneOffset.UTC);
        playerProfileService = new PlayerProfileService(playerProfileRepository, clock);
    }

    @Test
    void shouldMarkProfileCompleteWhenNicknameAndTwoContactsProvided() {
        UUID userId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-04-18T12:00:00Z");

        PlayerProfile existingProfile = PlayerProfile.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .email("user@example.com")
                .status(PlayerProfileStatus.INCOMPLETE)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();

        when(playerProfileRepository.findByUserId(userId)).thenReturn(Optional.of(existingProfile));
        when(playerProfileRepository.save(any(PlayerProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlayerProfileUpdateRequestDto request = new PlayerProfileUpdateRequestDto(
                "John",
                "Doe",
                "telegram_user",
                "vk_user",
                null,
                "game_nick"
        );

        PlayerProfile result = playerProfileService.updateProfile(userId, "user@example.com", request);

        assertEquals(PlayerProfileStatus.COMPLETE, result.getStatus());
        assertEquals("telegram_user", result.getTelegramUsername());
        assertEquals("vk_user", result.getVkUsername());
        assertEquals("game_nick", result.getCurrentGameNickname());
    }

    @Test
    void shouldRejectProfileUpdateWhenRequiredFieldsAreBlank() {
        UUID userId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-04-18T12:00:00Z");

        PlayerProfile existingProfile = PlayerProfile.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .email("user@example.com")
                .telegramUsername("oldTelegram")
                .status(PlayerProfileStatus.INCOMPLETE)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();

        when(playerProfileRepository.findByUserId(userId)).thenReturn(Optional.of(existingProfile));

        PlayerProfileUpdateRequestDto request = new PlayerProfileUpdateRequestDto(
                null,
                null,
                "   ",
                "vk_user",
                null,
                "   "
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> playerProfileService.updateProfile(userId, "user@example.com", request)
        );

        assertEquals("Game nickname is required", exception.getReason());
    }
}
