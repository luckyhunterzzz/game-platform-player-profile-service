package com.gameplatform.playerprofileservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PlayerWarAttackTeamsUpdateRequestDto(
        @NotNull
        @Size(min = 6, max = 6)
        List<@Valid PlayerWarAttackTeamUpdateRequestDto> teams
) {
}
