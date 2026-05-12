package com.gameplatform.playerprofileservice.dto.request;

import com.gameplatform.playerprofileservice.domain.enums.HeroPowerGrade;
import jakarta.validation.constraints.NotNull;

public record PlayerProfileHeroPowerGradeUpdateRequestDto(
        @NotNull
        HeroPowerGrade powerGrade
) {
}
