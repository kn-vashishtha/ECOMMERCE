package org.bootcamp.ecommerce.dto.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ViewCustomerProfileResponse(
        String id,
        @JsonProperty("firstname") String firstName,
        @JsonProperty("lastname") String lastName,
        @JsonProperty("activated") boolean isActivated,
        @JsonProperty("contactnumber") String contactNumber,
        @JsonProperty("image") String image
) {
}
