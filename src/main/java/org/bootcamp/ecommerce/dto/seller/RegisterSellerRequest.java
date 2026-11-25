package org.bootcamp.ecommerce.dto.seller;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.bootcamp.ecommerce.dto.address.AddressRequestSeller;

import static org.bootcamp.ecommerce.constants.Consts.PHONE_NUMBER_REGEX;

public record RegisterSellerRequest(

        @NotBlank(message = "{validation.email.blank}")
        @NotNull(message = "{validation.email.required}")
        String email,

        @NotBlank(message = "{validation.password.blank}")
        @NotNull(message = "{validation.password.required}")
        @Size(min = 8, max = 15, message = "{validation.password.size}")
        String password,

        @NotBlank(message = "{validation.confirmpassword.blank}")
        @NotNull(message = "{validation.confirmpassword.required}")
        @Size(min = 8, max = 15, message = "{validation.password.size}")
        String confirmPassword,

        @NotBlank(message = "{validation.firstname.blank}")
        @NotNull(message = "{validation.firstname.required}")
        String firstName,

        String middleName,

        @NotBlank(message = "{validation.lastname.blank}")
        @NotNull(message = "{validation.lastname.required}")
        String lastName,

        @NotBlank(message = "{validation.seller.gst.blank}")
        @NotNull(message = "{validation.seller.gst.required}")
        String gst,

        @NotBlank(message = "{validation.seller.contact.blank}")
        @NotNull(message = "{validation.seller.contact.required}")
        @Size(min = 10, max = 10, message = "{seller.contact.size}")
        @Pattern(regexp = PHONE_NUMBER_REGEX, message = "{validation.contactnumber.invalid}")
        @JsonProperty("contactNumber")
        String companyContactNumber,

        @NotBlank(message = "{validation.seller.companyname.blank}")
        @NotNull(message = "{validation.seller.companyname.required}")
        String companyName,

        @Valid
        @NotNull(message = "{validation.seller.address.required}")
        AddressRequestSeller address
) {
}
