package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class CategoryMetaDataFieldNotFoundException extends RuntimeException {
    public CategoryMetaDataFieldNotFoundException(String id) {
        super(MessageUtil.getMessagePlaceHolder("category.metadata.invalid", id));
    }
}
