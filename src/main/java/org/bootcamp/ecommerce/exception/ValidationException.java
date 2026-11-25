package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class ValidationException extends RuntimeException {
    public ValidationException(String key) {
        super(MessageUtil.getMessage(key));
    }

    public ValidationException(String key, String... values) {
        super(MessageUtil.getMessagePlaceHolder(key, values));
    }
}
