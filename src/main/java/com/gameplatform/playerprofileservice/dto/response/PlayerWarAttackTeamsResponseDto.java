package com.gameplatform.playerprofileservice.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PlayerWarAttackTeamsResponseDto(
        List<PlayerWarModeResponseDto> warModes,
        List<PlayerWarAttackTeamResponseDto> teams
) {
}
