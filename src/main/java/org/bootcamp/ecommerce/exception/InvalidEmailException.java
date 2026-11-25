package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String details) {
        super(MessageUtil.getMessage("validation.email.invalid"), new Throwable(details));
    }
}
