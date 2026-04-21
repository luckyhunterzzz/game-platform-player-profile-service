package com.gameplatform.playerprofileservice.dto.response;

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
    private OffsetDateTime createdAt;
}
