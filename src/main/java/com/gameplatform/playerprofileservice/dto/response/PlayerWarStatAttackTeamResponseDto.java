package com.gameplatform.playerprofileservice.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record PlayerWarStatAttackTeamResponseDto(
        UUID id,
        String name,
        Integer teamOrder,
        List<PlayerWarStatAttackTeamSlotResponseDto> slots,
        List<PlayerWarStatAttackRecordResponseDto> records
) {
}
