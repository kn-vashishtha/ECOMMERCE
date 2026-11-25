package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(String id) {
        super(MessageUtil.getMessagePlaceHolder("category.not.found", id));
    }
}
