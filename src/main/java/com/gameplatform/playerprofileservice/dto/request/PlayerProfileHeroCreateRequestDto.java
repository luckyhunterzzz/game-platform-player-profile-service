package com.gameplatform.playerprofileservice.dto.request;

import com.gameplatform.playerprofileservice.domain.enums.HeroPowerGrade;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PlayerProfileHeroCreateRequestDto(
        @NotNull
        Long heroId,
        HeroPowerGrade powerGrade,
        @Min(0)
        Integer costumeCount
) {
}
