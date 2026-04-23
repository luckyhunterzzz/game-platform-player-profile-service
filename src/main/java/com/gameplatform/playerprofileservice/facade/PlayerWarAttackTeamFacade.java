package com.gameplatform.playerprofileservice.facade;

import com.gameplatform.playerprofileservice.dto.request.PlayerWarAttackTeamsUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarAttackSlotResponseDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarAttackTeamResponseDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarAttackTeamsResponseDto;
import com.gameplatform.playerprofileservice.service.PlayerWarAttackTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlayerWarAttackTeamFacade {

    private final PlayerWarAttackTeamService playerWarAttackTeamService;

    public PlayerWarAttackTeamsResponseDto getMyTeams(UUID userId, String email) {
        return toResponse(playerWarAttackTeamService.getMyTeams(userId, email));
    }

    public PlayerWarAttackTeamsResponseDto updateMyTeams(UUID userId,
                                                         String email,
                                                         PlayerWarAttackTeamsUpdateRequestDto request) {
        return toResponse(playerWarAttackTeamService.updateMyTeams(userId, email, request));
    }

    private PlayerWarAttackTeamsResponseDto toResponse(List<PlayerWarAttackTeamService.WarAttackTeamView> teams) {
        return PlayerWarAttackTeamsResponseDto.builder()
                .teams(teams.stream()
                        .map(team -> PlayerWarAttackTeamResponseDto.builder()
                                .id(team.id())
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
