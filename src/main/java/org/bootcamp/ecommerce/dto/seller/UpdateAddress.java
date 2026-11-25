package org.bootcamp.ecommerce.dto.seller;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static org.bootcamp.ecommerce.constants.Consts.ADDRESS_REGEX;
import static org.bootcamp.ecommerce.constants.Consts.ZIPCODE_REGEX;

public record UpdateAddress(

        @NotNull(message = "{address.id.required}")
        @NotBlank(message = "{address.id.blank}")
        String id,
        @Size(min = 2, max = 50, message = "{address.city.size}")
        @Pattern(regexp = ADDRESS_REGEX, message = "{address.city.invalid}")
        String city,
        @Size(min = 2, max = 50, message = "{address.state.size}")
        @Pattern(regexp = ADDRESS_REGEX, message = "{address.state.invalid}")
        String state,
        @Size(min = 2, max = 50, message = "{address.country.size}")
        @Pattern(regexp = ADDRESS_REGEX, message = "{address.country.invalid}")
        String country,

        @Size(min = 5, max = 255, message = "{address.line.size}")
        @JsonProperty("addressline")
        String addressLine,

        @Pattern(regexp = ZIPCODE_REGEX, message = "{address.zip.invalid}")
        @JsonProperty("zipcode")
        String zipCode
) {
}
