package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException() {
        super(MessageUtil.getMessage("address.not.found"));
    }
}
