package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class CategoryIsNotLeafException extends RuntimeException {
    public CategoryIsNotLeafException(String id) {
        super(MessageUtil.getMessagePlaceHolder("category.is.not.leaf", id));
    }
}
