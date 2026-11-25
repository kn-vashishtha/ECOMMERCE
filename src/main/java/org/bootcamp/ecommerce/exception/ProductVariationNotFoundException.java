package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class ProductVariationNotFoundException extends RuntimeException {
    public ProductVariationNotFoundException(String id) {
        super(MessageUtil.getMessagePlaceHolder("product.variation.not.found", id));
    }
}
