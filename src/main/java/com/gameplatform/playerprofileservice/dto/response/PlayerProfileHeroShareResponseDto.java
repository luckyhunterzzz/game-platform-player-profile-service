package com.gameplatform.playerprofileservice.dto.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record PlayerProfileHeroShareResponseDto(
        UUID token,
        OffsetDateTime createdAt,
        OffsetDateTime expiresAt,
        List<PlayerProfileHeroShareItemResponseDto> heroes
) {
}
