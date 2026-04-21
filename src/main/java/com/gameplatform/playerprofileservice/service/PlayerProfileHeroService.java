package com.gameplatform.playerprofileservice.service;

import com.gameplatform.playerprofileservice.domain.entity.PlayerProfile;
import com.gameplatform.playerprofileservice.domain.entity.PlayerProfileHero;
import com.gameplatform.playerprofileservice.repository.PlayerProfileHeroRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerProfileHeroService {

    private final PlayerProfileService playerProfileService;
    private final PlayerProfileHeroRepository playerProfileHeroRepository;
    private final Clock clock;

    public List<PlayerProfileHero> getMyHeroes(UUID userId, String email) {
        PlayerProfile playerProfile = playerProfileService.getOrCreateProfile(userId, email);

        return playerProfileHeroRepository.findAllByPlayerProfileIdOrderByCreatedAtAsc(playerProfile.getId());
    }

    @Transactional
    public PlayerProfileHero addHero(UUID userId, String email, Long heroId) {
        PlayerProfile playerProfile = playerProfileService.getOrCreateProfile(userId, email);
        OffsetDateTime now = OffsetDateTime.now(clock);

        PlayerProfileHero playerProfileHero = PlayerProfileHero.builder()
                .id(UUID.randomUUID())
                .playerProfileId(playerProfile.getId())
                .heroId(heroId)
                .createdAt(now)
                .build();

        return playerProfileHeroRepository.save(playerProfileHero);
    }

    @Transactional
    public void deleteHero(UUID userId, String email, UUID profileHeroId) {
        PlayerProfile playerProfile = playerProfileService.getOrCreateProfile(userId, email);

        PlayerProfileHero playerProfileHero = playerProfileHeroRepository
                .findByIdAndPlayerProfileId(profileHeroId, playerProfile.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Profile hero not found with id: " + profileHeroId
                ));

        playerProfileHeroRepository.delete(playerProfileHero);
    }
}
