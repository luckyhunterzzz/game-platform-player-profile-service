package com.gameplatform.playerprofileservice.dto.request;

import jakarta.validation.constraints.NotNull;

public record PlayerProfileHeroCreateRequestDto(
        @NotNull
        Long heroId
) {
}
