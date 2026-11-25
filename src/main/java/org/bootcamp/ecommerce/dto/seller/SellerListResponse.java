package org.bootcamp.ecommerce.dto.seller;

import org.bootcamp.ecommerce.dto.address.AddressResponseDTO;

import java.util.UUID;

public record SellerListResponse(
        UUID id,
        String fullName,
        String email,
        boolean isActivated,
        String companyName,
        AddressResponseDTO companyAddress,
        String companyContactNumber
) {
    public SellerListResponse(UUID id, String firstName, String middleName, String lastName, String email, boolean isActivated, String companyName, AddressResponseDTO companyAddress, String companyContactNumber) {
        this(id, String.join(" ",
                firstName != null ? firstName : "",
                middleName != null ? middleName : "",
                lastName != null ? lastName : ""
        ).trim().replaceAll(" +", " "), email, isActivated, companyName, companyAddress, companyContactNumber);
    }
}
