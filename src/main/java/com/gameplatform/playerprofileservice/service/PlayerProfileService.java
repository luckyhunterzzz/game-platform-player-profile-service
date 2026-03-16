package com.gameplatform.playerprofileservice.service;

import com.gameplatform.playerprofileservice.domain.entity.PlayerProfile;
import com.gameplatform.playerprofileservice.domain.enums.PlayerProfileStatus;
import com.gameplatform.playerprofileservice.repository.PlayerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerProfileService {

    private final PlayerProfileRepository playerProfileRepository;
    private final Clock clock;

    @Transactional
    public PlayerProfile getOrCreateProfile(UUID userId, String email) {
        return playerProfileRepository.findByUserId(userId)
                .orElseGet(() -> createProfile(userId, email));
    }

    private PlayerProfile createProfile(UUID userId, String email) {
        OffsetDateTime now = OffsetDateTime.now(clock);

        PlayerProfile playerProfile = PlayerProfile.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .email(email)
                .status(PlayerProfileStatus.INCOMPLETE)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return playerProfileRepository.save(playerProfile);
    }
}
