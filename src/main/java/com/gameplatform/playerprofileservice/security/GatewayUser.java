package com.gameplatform.playerprofileservice.security;

import java.util.UUID;

public record GatewayUser(UUID userId, String email) {
}
