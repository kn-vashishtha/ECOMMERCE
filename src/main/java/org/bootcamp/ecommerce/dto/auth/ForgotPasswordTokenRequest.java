package org.bootcamp.ecommerce.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ForgotPasswordTokenRequest(
        @NotBlank(message = "{validation.email.blank}")
        @NotNull(message = "{validation.email.required}")
        String email
) {
}
