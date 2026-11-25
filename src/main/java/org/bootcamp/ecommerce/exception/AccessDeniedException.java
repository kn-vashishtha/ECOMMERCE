package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super(MessageUtil.getMessage("product.access-denied"));
    }
}
