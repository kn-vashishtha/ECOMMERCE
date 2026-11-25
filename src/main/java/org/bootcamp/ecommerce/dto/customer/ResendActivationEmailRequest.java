package org.bootcamp.ecommerce.dto.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResendActivationEmailRequest(
        @NotNull(message = "{validation.email.required}")
        @NotBlank(message = "{validation.email.blank}")
        String email
) {
}
