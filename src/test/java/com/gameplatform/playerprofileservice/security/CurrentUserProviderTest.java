package com.gameplatform.playerprofileservice.security;

import com.gameplatform.playerprofileservice.utility.HeaderNames;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrentUserProviderTest {

    @Test
    void readsUserFromGatewayHeaders() {
        UUID userId = UUID.randomUUID();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HeaderNames.USER_ID, userId.toString());
        request.addHeader(HeaderNames.USER_EMAIL, "user@example.com");

        GatewayUser user = new CurrentUserProvider(request).getCurrentUser();

        assertThat(user.userId()).isEqualTo(userId);
        assertThat(user.email()).isEqualTo("user@example.com");
    }

    @Test
    void rejectsMissingGatewayHeaders() {
        CurrentUserProvider provider = new CurrentUserProvider(new MockHttpServletRequest());

        assertThatThrownBy(provider::getCurrentUser)
                .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
                        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void rejectsInvalidGatewayUserId() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HeaderNames.USER_ID, "not-a-uuid");
        request.addHeader(HeaderNames.USER_EMAIL, "user@example.com");
        CurrentUserProvider provider = new CurrentUserProvider(request);

        assertThatThrownBy(provider::getCurrentUser)
                .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
                        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED));
    }
}
