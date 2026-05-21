package com.gameplatform.playerprofileservice.dto.request;

import com.gameplatform.playerprofileservice.domain.enums.WarStatAttackResultType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PlayerWarStatAttackRecordUpsertRequestDto(
        @NotBlank
        String warModeCode,
        @NotNull
        WarStatAttackResultType resultType,
        @NotNull
        LocalDate battleDate
) {
}
