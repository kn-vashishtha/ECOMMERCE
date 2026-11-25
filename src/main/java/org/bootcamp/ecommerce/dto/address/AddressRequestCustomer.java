package org.bootcamp.ecommerce.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.bootcamp.ecommerce.enums.Label;

import static org.bootcamp.ecommerce.constants.Consts.ADDRESS_REGEX;
import static org.bootcamp.ecommerce.constants.Consts.ZIPCODE_REGEX;

public record AddressRequestCustomer(
        @NotNull(message = "{address.city.required}")
        @NotBlank(message = "{address.city.blank}")
        @Size(min = 2, max = 50, message = "{address.city.size}")
        @Pattern(regexp = ADDRESS_REGEX, message = "{address.city.invalid}")
        String city,

        @NotNull(message = "{address.state.required}")
        @NotBlank(message = "{address.state.blank}")
        @Size(min = 2, max = 50, message = "{address.state.size}")
        @Pattern(regexp = ADDRESS_REGEX, message = "{address.state.invalid}")
        String state,

        @NotNull(message = "{address.country.required}")
        @NotBlank(message = "{address.country.blank}")
        @Size(min = 2, max = 50, message = "{address.country.size}")
        @Pattern(regexp = ADDRESS_REGEX, message = "{address.country.invalid}")
        String country,

        @NotNull(message = "{address.line.required}")
        @NotBlank(message = "{address.line.blank}")
        @Size(min = 5, max = 255, message = "{address.line.size}")
        String addressLine,

        @NotNull(message = "{address.zip.required}")
        @NotBlank(message = "{address.zip.blank}")
        @Pattern(regexp = ZIPCODE_REGEX, message = "{address.zip.invalid}")
        String zipCode,

        @NotNull(message = "{address.label.required}")
        Label label
) {
}
