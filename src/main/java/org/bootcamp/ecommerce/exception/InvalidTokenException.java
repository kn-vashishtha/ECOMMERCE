package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() {
        super(MessageUtil.getMessage("token.invalid"));
    }
}
