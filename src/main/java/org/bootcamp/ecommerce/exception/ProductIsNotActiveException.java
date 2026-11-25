package org.bootcamp.ecommerce.exception;


import org.bootcamp.ecommerce.utils.MessageUtil;

public class ProductIsNotActiveException extends RuntimeException {
    public ProductIsNotActiveException(String id) {
        super(MessageUtil.getMessagePlaceHolder("product.not.active", id));
    }
}
