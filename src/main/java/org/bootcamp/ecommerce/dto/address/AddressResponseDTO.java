package org.bootcamp.ecommerce.dto.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bootcamp.ecommerce.enums.Label;

public record AddressResponseDTO(
        @JsonProperty("address_id") String addressId,
        String city,
        String state,
        String country,
        String addressLine,
        String zipCode,
        Label label
) {
}
