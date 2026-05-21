package com.gameplatform.playerprofileservice.dto.response;

import lombok.Builder;

@Builder
public record PlayerWarModeResponseDto(
        String code,
        String nameRu,
        String nameEn,
        String descriptionRu,
        String descriptionEn,
        Integer sortOrder
) {
}
