package org.bootcamp.ecommerce.utils.validators;

import org.bootcamp.ecommerce.exception.InvalidEmailException;

public class EmailValidator {

    public static String validate(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email is empty";
        }

        email = email.trim();

        if (!email.contains("@")) {
            return "'@' symbol is missing";
        }

        int atIndex = email.indexOf('@');

        if (atIndex == 0) {
            return "Local part before '@' is missing";
        }

        if (atIndex != email.lastIndexOf('@')) {
            return "Email contains multiple '@' symbols";
        }

        String domainPart = email.substring(atIndex + 1);

        if (domainPart.isEmpty()) {
            return "Domain part after '@' is missing";
        }

        int dotIndex = domainPart.lastIndexOf('.');

        if (dotIndex <= 0) {
            return "'.' is missing in domain part";
        }

        if (dotIndex == domainPart.length() - 1) {
            return "Top-level domain is missing after '.'";
        }

        return "Valid";
    }

    public static void validateEmail(String email) {
        String isValid = validate(email);
        if (!"Valid".equals(isValid)) {
            throw new InvalidEmailException(isValid);
        }
    }

    private EmailValidator() {
    }
}
