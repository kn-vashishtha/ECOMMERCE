package org.bootcamp.ecommerce.dto.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static org.bootcamp.ecommerce.constants.Consts.PHONE_NUMBER_REGEX;

public record UpdateCustomerRequest(
        @JsonProperty("firstname") String firstName,
        @JsonProperty("middlename") String middleName,
        @JsonProperty("lastname") String lastName,
        @Size(min = 10, max = 10, message = "{validation.contactnumber.size}")
        @Pattern(regexp = PHONE_NUMBER_REGEX, message = "{validation.contactnumber.invalid}")
        @JsonProperty("contactnumber") String contactNumber

) {

}
