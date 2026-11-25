package org.bootcamp.ecommerce.dto.seller;

public record SellerResponse(
        String email,
        String companyName,
        String gstNumber,
        String firstName,
        String lastName,
        String companyContact,
        String responseMessage
) {
    public SellerResponse(String email, String companyName, String gstNumber, String firstName, String lastName, String companyContact) {
        this(email, companyName, gstNumber, firstName, lastName, companyContact,
                "Seller " + firstName + " " + lastName + " has been registered successfully.");
    }
}
