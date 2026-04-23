package com.gameplatform.playerprofileservice.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record PlayerWarAttackTeamResponseDto(
        UUID id,
        Integer teamIndex,
        List<PlayerWarAttackSlotResponseDto> slots
) {
}
