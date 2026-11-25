package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super(MessageUtil.getMessage("user.not.found"));
    }
}
