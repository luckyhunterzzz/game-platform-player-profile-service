package com.gameplatform.playerprofileservice.converter;

import com.gameplatform.playerprofileservice.domain.entity.PlayerProfile;
import com.gameplatform.playerprofileservice.dto.response.PlayerProfileResponseDto;
import org.springframework.stereotype.Component;

@Component
public class PlayerProfileResponseConverter {

    public PlayerProfileResponseDto toResponse(PlayerProfile playerProfile) {
        return PlayerProfileResponseDto.builder()
                .id(playerProfile.getId())
                .userId(playerProfile.getUserId())
                .email(playerProfile.getEmail())
                .firstName(playerProfile.getFirstName())
                .lastName(playerProfile.getLastName())
                .telegramUsername(playerProfile.getTelegramUsername())
                .vkUsername(playerProfile.getVkUsername())
                .discordUsername(playerProfile.getDiscordUsername())
                .currentGameNickname(playerProfile.getCurrentGameNickname())
                .status(playerProfile.getStatus())
                .createdAt(playerProfile.getCreatedAt())
                .updatedAt(playerProfile.getUpdatedAt())
                .build();
    }
}
