package com.gameplatform.playerprofileservice.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PlayerWarStatAttackTeamSlotResponseDto(
        Integer slot,
        UUID playerProfileHeroId
) {
}
