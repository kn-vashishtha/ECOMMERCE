package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class CategoryMetadataFieldValuesAlreadyExistsException extends RuntimeException {
    public CategoryMetadataFieldValuesAlreadyExistsException(String field,String value) {
        super(MessageUtil.getMessagePlaceHolder("category.metadata.field.values.duplicate", value));
    }
}
