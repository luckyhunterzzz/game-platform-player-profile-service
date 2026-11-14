package com.gameplatform.playerprofileservice.dto.response;

import com.gameplatform.playerprofileservice.domain.enums.HeroPowerGrade;

public record PlayerProfileHeroShareItemResponseDto(
        Long heroId,
        HeroPowerGrade powerGrade,
        Integer talentLevel,
        Integer costumeCount,
        Integer order
) {
}
