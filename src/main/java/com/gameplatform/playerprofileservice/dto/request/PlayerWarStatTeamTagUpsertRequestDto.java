package com.gameplatform.playerprofileservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlayerWarStatTeamTagUpsertRequestDto(
        @NotBlank
        @Size(max = 50)
        String name,
        @NotBlank
        @Size(max = 128)
        String iconKey,
        @NotBlank
        @Size(max = 500)
        String imageUrl
) {
}
