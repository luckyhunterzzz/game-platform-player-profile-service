package com.gameplatform.playerprofileservice.facade;

import com.gameplatform.playerprofileservice.configuration.CacheNames;
import com.gameplatform.playerprofileservice.configuration.ProfileCacheEvictionService;
import com.gameplatform.playerprofileservice.converter.PlayerProfileHeroResponseConverter;
import com.gameplatform.playerprofileservice.domain.entity.PlayerProfileHero;
import com.gameplatform.playerprofileservice.dto.request.PlayerProfileHeroCreateRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerProfileHeroPowerGradeUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerProfileHeroTalentLevelUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerProfileHeroResponseDto;
import com.gameplatform.playerprofileservice.service.PlayerProfileHeroService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlayerProfileHeroFacade {

    private final PlayerProfileHeroService playerProfileHeroService;
    private final PlayerProfileHeroResponseConverter playerProfileHeroResponseConverter;
    private final ProfileCacheEvictionService profileCacheEvictionService;

    @Cacheable(cacheNames = CacheNames.MY_PROFILE_HEROES)
    public List<PlayerProfileHeroResponseDto> getMyHeroes(UUID userId, String email) {
        return playerProfileHeroService.getMyHeroes(userId, email).stream()
                .map(playerProfileHeroResponseConverter::toResponse)
                .toList();
    }

    public PlayerProfileHeroResponseDto addHero(UUID userId,
                                                String email,
                                                PlayerProfileHeroCreateRequestDto request) {
        PlayerProfileHero playerProfileHero = playerProfileHeroService.addHero(
                userId,
                email,
                request.heroId(),
                request.powerGrade()
        );
        PlayerProfileHeroResponseDto response = playerProfileHeroResponseConverter.toResponse(playerProfileHero);
        profileCacheEvictionService.evictAllProfileCaches();
        return response;
    }

    public PlayerProfileHeroResponseDto updateHeroPowerGrade(UUID userId,
                                                             String email,
                                                             UUID profileHeroId,
                                                             PlayerProfileHeroPowerGradeUpdateRequestDto request) {
        PlayerProfileHero playerProfileHero = playerProfileHeroService.updateHeroPowerGrade(
                userId,
                email,
                profileHeroId,
                request.powerGrade()
        );
        PlayerProfileHeroResponseDto response = playerProfileHeroResponseConverter.toResponse(playerProfileHero);
        profileCacheEvictionService.evictAllProfileCaches();
        return response;
    }

    public PlayerProfileHeroResponseDto updateHeroTalentLevel(UUID userId,
                                                              String email,
                                                              UUID profileHeroId,
                                                              PlayerProfileHeroTalentLevelUpdateRequestDto request) {
        PlayerProfileHero playerProfileHero = playerProfileHeroService.updateHeroTalentLevel(
                userId,
                email,
                profileHeroId,
                request.talentLevel()
        );
        PlayerProfileHeroResponseDto response = playerProfileHeroResponseConverter.toResponse(playerProfileHero);
        profileCacheEvictionService.evictAllProfileCaches();
        return response;
    }

    public void deleteHero(UUID userId, String email, UUID profileHeroId) {
        playerProfileHeroService.deleteHero(userId, email, profileHeroId);
        profileCacheEvictionService.evictAllProfileCaches();
    }
}
