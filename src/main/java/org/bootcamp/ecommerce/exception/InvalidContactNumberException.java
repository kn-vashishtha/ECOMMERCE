package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class InvalidContactNumberException extends RuntimeException {
    public InvalidContactNumberException() {
        super(MessageUtil.getMessage("contact.invalid"));
    }
}
