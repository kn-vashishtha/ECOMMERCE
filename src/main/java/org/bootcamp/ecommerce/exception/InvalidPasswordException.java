package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super(MessageUtil.getMessage("validation.password.invalid"));
    }
}
