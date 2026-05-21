package com.gameplatform.playerprofileservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PlayerWarStatAttackTeamUpdateRequestDto(
        @NotBlank
        @Size(max = 128)
        String name,
        @NotNull
        @Size(min = 5, max = 5)
        List<@Valid PlayerWarStatAttackTeamSlotUpdateRequestDto> slots
) {
}
