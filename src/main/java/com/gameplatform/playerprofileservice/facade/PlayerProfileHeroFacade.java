package com.gameplatform.playerprofileservice.facade;

import com.gameplatform.playerprofileservice.converter.PlayerProfileHeroResponseConverter;
import com.gameplatform.playerprofileservice.domain.entity.PlayerProfileHero;
import com.gameplatform.playerprofileservice.dto.request.PlayerProfileHeroCreateRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerProfileHeroPowerGradeUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerProfileHeroTalentLevelUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerProfileHeroResponseDto;
import com.gameplatform.playerprofileservice.service.PlayerProfileHeroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlayerProfileHeroFacade {

    private final PlayerProfileHeroService playerProfileHeroService;
    private final PlayerProfileHeroResponseConverter playerProfileHeroResponseConverter;

    public List<PlayerProfileHeroResponseDto> getMyHeroes(UUID userId, String email) {
        return playerProfileHeroService.getMyHeroes(userId, email).stream()
                .map(playerProfileHeroResponseConverter::toResponse)
                .toList();
    }

    public PlayerProfileHeroResponseDto addHero(UUID userId,
                                                String email,
                                                PlayerProfileHeroCreateRequestDto request) {
        PlayerProfileHero playerProfileHero = playerProfileHeroService.addHero(userId, email, request.heroId());
        return playerProfileHeroResponseConverter.toResponse(playerProfileHero);
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
        return playerProfileHeroResponseConverter.toResponse(playerProfileHero);
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
        return playerProfileHeroResponseConverter.toResponse(playerProfileHero);
    }

    public void deleteHero(UUID userId, String email, UUID profileHeroId) {
        playerProfileHeroService.deleteHero(userId, email, profileHeroId);
    }
}
