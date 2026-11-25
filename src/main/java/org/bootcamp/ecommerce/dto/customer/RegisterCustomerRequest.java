package org.bootcamp.ecommerce.dto.customer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.bootcamp.ecommerce.dto.address.AddressRequestCustomer;

import java.util.List;

import static org.bootcamp.ecommerce.constants.Consts.PHONE_NUMBER_REGEX;

public record RegisterCustomerRequest(

        @NotBlank(message = "{validation.email.blank}")
        @NotNull(message = "{validation.email.required}")
        String email,

        @NotBlank(message = "{validation.password.blank}")
        @NotNull(message = "{validation.password.required}")
        @Size(min = 8, max = 15, message = "{validation.password.size}")
        String password,

        @NotBlank(message = "{validation.confirmpassword.blank}")
        @NotNull(message = "{validation.confirmpassword.required}")
        @Size(min = 8, max = 15, message = "{validation.confirmpassword.size}")
        String confirmPassword,

        @NotBlank(message = "{validation.contactnumber.blank}")
        @NotNull(message = "{validation.contactnumber.required}")
        @Size(min = 10, max = 10, message = "{validation.contactnumber.size}")
        @Pattern(regexp = PHONE_NUMBER_REGEX, message = "{validation.contactnumber.invalid}")
        String contactNumber,

        @NotBlank(message = "{validation.firstname.blank}")
        @NotNull(message = "{validation.firstname.required}")
        String firstName,

        String middleName,

        @NotBlank(message = "{validation.lastname.blank}")
        @NotNull(message = "{validation.lastname.required}")
        String lastName,

        @Valid
        @NotNull(message = "{validation.address.required}")
        List<AddressRequestCustomer> address
) {
}
