package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class CategoryMetaDataFieldValuesNotFoundException extends RuntimeException {
    public CategoryMetaDataFieldValuesNotFoundException(String categoryId, String fieldId) {
        super(MessageUtil.getMessagePlaceHolder("category.metadata.field.duplicate", categoryId, fieldId));
    }
}
