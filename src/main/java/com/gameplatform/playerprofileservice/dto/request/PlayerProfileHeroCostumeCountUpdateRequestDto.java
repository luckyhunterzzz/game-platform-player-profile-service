package com.gameplatform.playerprofileservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PlayerProfileHeroCostumeCountUpdateRequestDto(
        @NotNull
        @Min(0)
        Integer costumeCount
) {
}
