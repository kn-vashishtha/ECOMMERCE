package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class AccountLockedException extends RuntimeException {
    public AccountLockedException() {
        super(MessageUtil.getMessage("user.account.locked"));
    }
}
