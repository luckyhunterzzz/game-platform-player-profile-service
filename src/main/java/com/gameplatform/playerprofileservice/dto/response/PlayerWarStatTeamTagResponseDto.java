package com.gameplatform.playerprofileservice.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PlayerWarStatTeamTagResponseDto(
        UUID id,
        String scopeType,
        String category,
        String code,
        String name,
        String iconKey,
        String imageUrl
) {
}
