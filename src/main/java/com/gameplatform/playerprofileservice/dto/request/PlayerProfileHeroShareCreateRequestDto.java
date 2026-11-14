package com.gameplatform.playerprofileservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record PlayerProfileHeroShareCreateRequestDto(
        @NotEmpty
        @Size(max = 1000)
        List<@NotNull UUID> profileHeroIds,
        Integer expiresInDays
) {
}
