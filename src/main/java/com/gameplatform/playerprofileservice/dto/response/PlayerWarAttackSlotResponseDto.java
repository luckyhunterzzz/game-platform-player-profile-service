package com.gameplatform.playerprofileservice.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PlayerWarAttackSlotResponseDto(
        Integer slot,
        UUID playerProfileHeroId
) {
}
