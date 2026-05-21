package com.gameplatform.playerprofileservice.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PlayerWarStatAttackTeamsResponseDto(
        List<PlayerWarModeResponseDto> warModes,
        List<PlayerWarStatAttackTeamResponseDto> teams
) {
}
