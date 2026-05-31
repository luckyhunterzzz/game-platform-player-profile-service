package com.gameplatform.playerprofileservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record PlayerWarStatTeamTagsUpdateRequestDto(
        @NotNull
        @Size(max = 7)
        List<@NotNull UUID> tagIds
) {
}
