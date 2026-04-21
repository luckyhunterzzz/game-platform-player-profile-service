package com.gameplatform.playerprofileservice.dto.request;

import jakarta.validation.constraints.Size;

public record PlayerProfileUpdateRequestDto(
        @Size(max = 100)
        String firstName,

        @Size(max = 100)
        String lastName,

        @Size(max = 100)
        String telegramUsername,

        @Size(max = 100)
        String vkUsername,

        @Size(max = 100)
        String discordUsername,

        @Size(max = 100)
        String currentGameNickname
) {
}
