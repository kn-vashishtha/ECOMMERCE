package org.bootcamp.ecommerce.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginResponse(
        @NotNull(message = "{validation.accesstoken.required}")
        @NotBlank(message = "{validation.accesstoken.blank}")
        String accessToken,

        @NotNull(message = "{validation.refreshtoken.required}")
        @NotBlank(message = "{validation.refreshtoken.blank}")
        String refreshToken
) {
}
