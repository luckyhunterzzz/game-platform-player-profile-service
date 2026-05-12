package com.gameplatform.playerprofileservice.dto.response;

import com.gameplatform.playerprofileservice.domain.enums.HeroPowerGrade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerProfileHeroResponseDto {
    private UUID id;
    private UUID playerProfileId;
    private Long heroId;
    private HeroPowerGrade powerGrade;
    private Integer talentLevel;
    private OffsetDateTime createdAt;
}
