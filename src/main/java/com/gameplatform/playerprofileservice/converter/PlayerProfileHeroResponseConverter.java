package com.gameplatform.playerprofileservice.converter;

import com.gameplatform.playerprofileservice.domain.entity.PlayerProfileHero;
import com.gameplatform.playerprofileservice.dto.response.PlayerProfileHeroResponseDto;
import org.springframework.stereotype.Component;

@Component
public class PlayerProfileHeroResponseConverter {

    public PlayerProfileHeroResponseDto toResponse(PlayerProfileHero playerProfileHero) {
        return PlayerProfileHeroResponseDto.builder()
                .id(playerProfileHero.getId())
                .playerProfileId(playerProfileHero.getPlayerProfileId())
                .heroId(playerProfileHero.getHeroId())
                .createdAt(playerProfileHero.getCreatedAt())
                .build();
    }
}
