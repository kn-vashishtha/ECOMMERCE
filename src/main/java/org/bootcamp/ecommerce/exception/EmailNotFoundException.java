package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException() {
        super(MessageUtil.getMessage("email.not.found"));
    }
}
