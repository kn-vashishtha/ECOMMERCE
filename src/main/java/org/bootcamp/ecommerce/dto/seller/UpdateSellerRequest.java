package org.bootcamp.ecommerce.dto.seller;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateSellerRequest(
        String firstName,
        String middleName,
        String lastName,
        String gst,
        @JsonProperty() String companyContactNumber,
        String companyName
) {
}

