package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class DuplicateFieldValuesException extends RuntimeException {
    public DuplicateFieldValuesException(String values) {
        super(MessageUtil.getMessagePlaceHolder("category.metadata.fieldvalues.invalid", values));
    }
}
