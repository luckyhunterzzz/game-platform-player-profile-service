package com.gameplatform.playerprofileservice.facade;

import com.gameplatform.playerprofileservice.converter.PlayerProfileResponseConverter;
import com.gameplatform.playerprofileservice.domain.entity.PlayerProfile;
import com.gameplatform.playerprofileservice.dto.request.PlayerProfileUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerProfileResponseDto;
import com.gameplatform.playerprofileservice.service.PlayerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlayerProfileFacade {

    private final PlayerProfileService playerProfileService;
    private final PlayerProfileResponseConverter playerProfileResponseConverter;

    public PlayerProfileResponseDto getOrCreateMyProfile(UUID userId, String email) {
        PlayerProfile playerProfile = playerProfileService.getOrCreateProfile(userId, email);
        return playerProfileResponseConverter.toResponse(playerProfile);
    }

    public PlayerProfileResponseDto updateMyProfile(UUID userId,
                                                    String email,
                                                    PlayerProfileUpdateRequestDto request) {
        PlayerProfile playerProfile = playerProfileService.updateProfile(userId, email, request);
        return playerProfileResponseConverter.toResponse(playerProfile);
    }

    public PlayerProfileResponseDto getProfileByUserId(UUID userId) {
        PlayerProfile playerProfile = playerProfileService.getProfileByUserId(userId);
        return playerProfileResponseConverter.toResponse(playerProfile);
    }
}
