package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException() {
        super(MessageUtil.getMessage("token.expired"));
    }
}
