package com.gameplatform.playerprofileservice.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PlayerWarStatTeamTagCatalogResponseDto(
        Integer teamTagLimit,
        Integer customTagLimit,
        List<PlayerWarStatTeamTagResponseDto> items
) {
}
