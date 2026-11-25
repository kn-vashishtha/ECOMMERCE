package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class CategoryMetaDataFieldAlreadyExistsException extends RuntimeException {
    public CategoryMetaDataFieldAlreadyExistsException(String field) {
        super(MessageUtil.getMessagePlaceHolder("category.metadata.field.duplicate", field));
    }
}
