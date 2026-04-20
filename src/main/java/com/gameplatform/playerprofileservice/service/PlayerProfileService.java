package com.gameplatform.playerprofileservice.service;

import com.gameplatform.playerprofileservice.domain.entity.PlayerProfile;
import com.gameplatform.playerprofileservice.domain.enums.PlayerProfileStatus;
import com.gameplatform.playerprofileservice.dto.request.PlayerProfileUpdateRequestDto;
import com.gameplatform.playerprofileservice.repository.PlayerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerProfileService {

    private static final int MIN_FILLED_CONTACTS_FOR_COMPLETE = 2;

    private final PlayerProfileRepository playerProfileRepository;
    private final Clock clock;

    @Transactional
    public PlayerProfile getOrCreateProfile(UUID userId, String email) {
        return playerProfileRepository.findByUserId(userId)
                .map(existingProfile -> syncEmailIfChanged(existingProfile, email))
                .orElseGet(() -> createProfile(userId, email));
    }

    @Transactional
    public PlayerProfile updateProfile(UUID userId, String email, PlayerProfileUpdateRequestDto request) {
        PlayerProfile playerProfile = getOrCreateProfile(userId, email);

        playerProfile.setFirstName(normalize(request.firstName()));
        playerProfile.setLastName(normalize(request.lastName()));
        playerProfile.setTelegramUsername(normalize(request.telegramUsername()));
        playerProfile.setVkUsername(normalize(request.vkUsername()));
        playerProfile.setDiscordUsername(normalize(request.discordUsername()));
        playerProfile.setCurrentGameNickname(normalize(request.currentGameNickname()));
        playerProfile.setStatus(calculateStatus(playerProfile));
        playerProfile.setUpdatedAt(OffsetDateTime.now(clock));

        return playerProfileRepository.save(playerProfile);
    }

    private PlayerProfile createProfile(UUID userId, String email) {
        OffsetDateTime now = OffsetDateTime.now(clock);

        PlayerProfile playerProfile = PlayerProfile.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .email(normalize(email))
                .status(PlayerProfileStatus.INCOMPLETE)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return playerProfileRepository.save(playerProfile);
    }

    private PlayerProfile syncEmailIfChanged(PlayerProfile playerProfile, String email) {
        String normalizedEmail = normalize(email);

        if (StringUtils.hasText(normalizedEmail) && !normalizedEmail.equals(playerProfile.getEmail())) {
            playerProfile.setEmail(normalizedEmail);
            playerProfile.setUpdatedAt(OffsetDateTime.now(clock));
            return playerProfileRepository.save(playerProfile);
        }

        return playerProfile;
    }

    private PlayerProfileStatus calculateStatus(PlayerProfile playerProfile) {
        boolean hasGameNickname = StringUtils.hasText(playerProfile.getCurrentGameNickname());
        int filledContactCount = countFilledContacts(playerProfile);

        return hasGameNickname && filledContactCount >= MIN_FILLED_CONTACTS_FOR_COMPLETE
                ? PlayerProfileStatus.COMPLETE
                : PlayerProfileStatus.INCOMPLETE;
    }

    private int countFilledContacts(PlayerProfile playerProfile) {
        int filledContacts = 0;

        if (StringUtils.hasText(playerProfile.getTelegramUsername())) {
            filledContacts++;
        }

        if (StringUtils.hasText(playerProfile.getVkUsername())) {
            filledContacts++;
        }

        if (StringUtils.hasText(playerProfile.getDiscordUsername())) {
            filledContacts++;
        }

        return filledContacts;
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        return value.trim();
    }
}
