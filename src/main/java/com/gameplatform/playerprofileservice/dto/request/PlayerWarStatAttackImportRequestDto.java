package com.gameplatform.playerprofileservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlayerWarStatAttackImportRequestDto(
        @NotBlank
        String warModeCode,
        @NotNull
        @Min(1)
        @Max(6)
        Integer teamIndex
) {
}
