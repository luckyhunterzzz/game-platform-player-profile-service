package com.gameplatform.playerprofileservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PlayerProfileHeroTalentLevelUpdateRequestDto(
        @NotNull
        @Min(0)
        @Max(25)
        Integer talentLevel
) {
}
