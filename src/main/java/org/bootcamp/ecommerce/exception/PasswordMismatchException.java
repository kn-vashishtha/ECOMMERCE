package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException() {
        super(MessageUtil.getMessage("validation.password.mismatch"));
    }
}
