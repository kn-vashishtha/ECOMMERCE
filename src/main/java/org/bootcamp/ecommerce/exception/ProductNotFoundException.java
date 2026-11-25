package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String id) {
        super(MessageUtil.getMessagePlaceHolder("product.not.found", id));
    }
}
