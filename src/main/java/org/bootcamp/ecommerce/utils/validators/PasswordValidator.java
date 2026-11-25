package org.bootcamp.ecommerce.utils.validators;

import org.bootcamp.ecommerce.exception.InvalidPasswordException;
import org.bootcamp.ecommerce.exception.PasswordMismatchException;

public class PasswordValidator {

    public static boolean isValidPassword(String password) {
        if (password == null) return false;

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;
        boolean noWhitespace = true;
        boolean minLength = password.length() >= 8;

        String specialCharacters = "@#$%^&+=!";

        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) hasUppercase = true;
            else if (Character.isLowerCase(ch)) hasLowercase = true;
            else if (Character.isDigit(ch)) hasDigit = true;
            else if (specialCharacters.indexOf(ch) >= 0) hasSpecialChar = true;
            else if (Character.isWhitespace(ch)) noWhitespace = false;
        }
        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar && noWhitespace && minLength;
    }

    public static boolean doPasswordsMatch(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) return false;
        return password.equals(confirmPassword);
    }

    public static String validateAndReturnPassword(String password, String confirmPassword) {
        if (!isValidPassword(password)) {
            throw new InvalidPasswordException();
        }
        if (!doPasswordsMatch(password, confirmPassword)) {
            throw new PasswordMismatchException();
        }
        return password;
    }

    private PasswordValidator() {
    }
}
