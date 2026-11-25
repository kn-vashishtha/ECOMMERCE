package org.bootcamp.ecommerce.dto.seller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bootcamp.ecommerce.dto.address.AddressResponseDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ViewSellerProfileResponse(
        String id,
        @JsonProperty("firstname") String firstName,
        @JsonProperty("lastname") String lastName,
        @JsonProperty("activated") boolean isActivated,
        @JsonProperty("companycontact") String companyContact,
        @JsonProperty("companyname") String companyName,
        @JsonProperty("image") String image,
        @JsonProperty("gstnumber") String gstNumber,
        @JsonProperty("address") AddressResponseDTO addressResponseDTO
) {
}
