package com.gameplatform.playerprofileservice.dto.response;

import com.gameplatform.playerprofileservice.domain.enums.WarStatAttackResultType;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record PlayerWarStatAttackRecordResponseDto(
        UUID id,
        String warModeCode,
        WarStatAttackResultType resultType,
        LocalDate battleDate
) {
}
