package org.bootcamp.ecommerce.dto.customer;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CustomerListResponse(
        UUID id,
        String fullName,
        String email,
        boolean isActivated
) {
    public CustomerListResponse(UUID id, String firstName, String middleName, String lastName, String email, boolean isActivated) {
        this(id, String.join(" ",
                firstName != null ? firstName : "",
                middleName != null ? middleName : "",
                lastName != null ? lastName : ""
        ).trim().replaceAll(" +", " "), email, isActivated);
    }
}
