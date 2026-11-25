package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class TokenBlacklistedException extends RuntimeException {
    public TokenBlacklistedException() {
        super(MessageUtil.getMessage("token.blacklisted"));
    }
}
