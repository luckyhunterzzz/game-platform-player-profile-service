package com.gameplatform.playerprofileservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PlayerWarAttackTeamUpdateRequestDto(
        @NotBlank
        String warModeCode,
        @NotNull
        @Min(1)
        @Max(6)
        Integer teamIndex,
        @NotNull
        @Size(min = 5, max = 5)
        List<@Valid PlayerWarAttackSlotUpdateRequestDto> slots
) {
}
