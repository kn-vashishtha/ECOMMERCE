package org.bootcamp.ecommerce.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdatePassword(
        @NotNull(message = "{validation.password.required}")
        @NotBlank(message = "{validation.password.blank}")
        @Size(min = 8, max = 64, message = "{validation.password.size}")
        String password,
        @NotNull(message = "{validation.confirmpassword.required}")
        @NotBlank(message = "{validation.confirmpassword.blank}")
        @Size(min = 8, max = 64, message = "{validation.confirmpassword.size}")
        String confirmPassword
) {
}
