package com.gameplatform.playerprofileservice.facade;

import com.gameplatform.playerprofileservice.configuration.CacheNames;
import com.gameplatform.playerprofileservice.configuration.ProfileCacheEvictionService;
import com.gameplatform.playerprofileservice.converter.PlayerProfileResponseConverter;
import com.gameplatform.playerprofileservice.domain.entity.PlayerProfile;
import com.gameplatform.playerprofileservice.dto.request.PlayerProfileUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerProfileResponseDto;
import com.gameplatform.playerprofileservice.service.PlayerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlayerProfileFacade {

    private final PlayerProfileService playerProfileService;
    private final PlayerProfileResponseConverter playerProfileResponseConverter;
    private final ProfileCacheEvictionService profileCacheEvictionService;

    public PlayerProfileResponseDto initializeMyProfile(UUID userId, String email) {
        PlayerProfile playerProfile = playerProfileService.initializeProfile(userId, email);
        PlayerProfileResponseDto response = playerProfileResponseConverter.toResponse(playerProfile);
        profileCacheEvictionService.evictAllProfileCaches();
        return response;
    }

    @Cacheable(cacheNames = CacheNames.MY_PROFILE)
    public PlayerProfileResponseDto getMyProfile(UUID userId, String email) {
        PlayerProfile playerProfile = playerProfileService.getRequiredProfile(userId, email);
        return playerProfileResponseConverter.toResponse(playerProfile);
    }

    public PlayerProfileResponseDto updateMyProfile(UUID userId,
                                                    String email,
                                                    PlayerProfileUpdateRequestDto request) {
        PlayerProfile playerProfile = playerProfileService.updateProfile(userId, email, request);
        PlayerProfileResponseDto response = playerProfileResponseConverter.toResponse(playerProfile);
        profileCacheEvictionService.evictAllProfileCaches();
        return response;
    }

    @Cacheable(cacheNames = CacheNames.PROFILE_BY_USER_ID)
    public PlayerProfileResponseDto getProfileByUserId(UUID userId) {
        PlayerProfile playerProfile = playerProfileService.getProfileByUserId(userId);
        return playerProfileResponseConverter.toResponse(playerProfile);
    }
}
