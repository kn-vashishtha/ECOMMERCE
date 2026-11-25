package org.bootcamp.ecommerce.dto.customer;

import java.util.UUID;

public record CustomerResponseRegister(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String responseMessage
) {
    public CustomerResponseRegister(UUID id, String email, String firstName, String lastName) {
        this(id, email, firstName, lastName,
                "Customer " + firstName + " " + lastName + " has been registered successfully.");
    }
}
