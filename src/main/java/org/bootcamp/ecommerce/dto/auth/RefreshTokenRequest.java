package org.bootcamp.ecommerce.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RefreshTokenRequest(
        @NotNull(message = "{validation.refreshtoken.required}")
        @NotBlank(message = "{validation.refreshtoken.blank}")
        String refreshToken) {
}
