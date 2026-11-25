package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException() {
        super(MessageUtil.getMessage("email.duplicate"));
    }
}
