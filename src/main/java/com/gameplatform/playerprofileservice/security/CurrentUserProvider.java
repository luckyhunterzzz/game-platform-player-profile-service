package com.gameplatform.playerprofileservice.security;

import com.gameplatform.playerprofileservice.utility.HeaderNames;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final HttpServletRequest request;

    public GatewayUser getCurrentUser() {
        String userIdHeader = request.getHeader(HeaderNames.USER_ID);
        String email = request.getHeader(HeaderNames.USER_EMAIL);

        if (userIdHeader == null || userIdHeader.isBlank() || email == null || email.isBlank()) {
            throw unauthorized("Gateway user context is missing");
        }

        try {
            return new GatewayUser(UUID.fromString(userIdHeader), email);
        } catch (IllegalArgumentException exception) {
            throw unauthorized("Gateway user ID is invalid");
        }
    }

    private ResponseStatusException unauthorized(String reason) {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, reason);
    }
}
