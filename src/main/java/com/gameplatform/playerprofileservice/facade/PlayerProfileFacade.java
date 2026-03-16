package com.gameplatform.playerprofileservice.facade;

import com.gameplatform.playerprofileservice.domain.entity.PlayerProfile;
import com.gameplatform.playerprofileservice.dto.response.PlayerProfileResponseDto;
import com.gameplatform.playerprofileservice.mapper.PlayerProfileMapper;
import com.gameplatform.playerprofileservice.service.PlayerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlayerProfileFacade {

    private final PlayerProfileService playerProfileService;
    private final PlayerProfileMapper playerProfileMapper;

    public PlayerProfileResponseDto getOrCreateMyProfile(UUID userId, String email) {
        PlayerProfile playerProfile = playerProfileService.getOrCreateProfile(userId, email);
        return playerProfileMapper.toResponseDto(playerProfile);
    }
}