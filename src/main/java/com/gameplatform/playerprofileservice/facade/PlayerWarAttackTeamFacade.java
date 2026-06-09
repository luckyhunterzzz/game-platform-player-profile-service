package com.gameplatform.playerprofileservice.facade;

import com.gameplatform.playerprofileservice.configuration.CacheNames;
import com.gameplatform.playerprofileservice.configuration.ProfileCacheEvictionService;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarAttackTeamsUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarAttackSlotResponseDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarAttackTeamResponseDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarAttackTeamsResponseDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarModeResponseDto;
import com.gameplatform.playerprofileservice.service.PlayerWarAttackTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlayerWarAttackTeamFacade {

    private final PlayerWarAttackTeamService playerWarAttackTeamService;
    private final ProfileCacheEvictionService profileCacheEvictionService;

    @Cacheable(cacheNames = CacheNames.MY_WAR_ATTACK_TEAMS)
    public PlayerWarAttackTeamsResponseDto getMyTeams(UUID userId, String email) {
        PlayerWarAttackTeamService.WarAttackTeamsView teamsView = playerWarAttackTeamService.getMyTeams(userId, email);
        return toResponse(teamsView);
    }

    public PlayerWarAttackTeamsResponseDto updateMyTeams(UUID userId,
                                                         String email,
                                                         PlayerWarAttackTeamsUpdateRequestDto request) {
        PlayerWarAttackTeamService.WarAttackTeamsView teamsView = playerWarAttackTeamService.updateMyTeams(userId, email, request);
        PlayerWarAttackTeamsResponseDto response = toResponse(teamsView);
        profileCacheEvictionService.evictAllProfileCaches();
        return response;
    }

    private PlayerWarAttackTeamsResponseDto toResponse(PlayerWarAttackTeamService.WarAttackTeamsView teamsView) {
        return PlayerWarAttackTeamsResponseDto.builder()
                .warModes(teamsView.warModes().stream()
                        .map(mode -> PlayerWarModeResponseDto.builder()
                                .code(mode.code())
                                .nameRu(mode.nameRu())
                                .nameEn(mode.nameEn())
                                .descriptionRu(mode.descriptionRu())
                                .descriptionEn(mode.descriptionEn())
                                .sortOrder(mode.sortOrder())
                                .build())
                        .toList())
                .teams(teamsView.teams().stream()
                        .map(team -> PlayerWarAttackTeamResponseDto.builder()
                                .id(team.id())
                                .warModeCode(team.warModeCode())
                                .teamIndex(team.teamIndex())
                                .slots(team.slots().stream()
                                        .map(slot -> PlayerWarAttackSlotResponseDto.builder()
                                                .slot(slot.slot())
                                                .playerProfileHeroId(slot.playerProfileHeroId())
                                                .build())
                                        .toList())
                                .build())
                        .toList())
                .build();
    }
}
