package com.gameplatform.playerprofileservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.UUID;

public record PlayerWarAttackSlotUpdateRequestDto(
        @Min(1)
        @Max(5)
        Integer slot,
        UUID playerProfileHeroId
) {
}
