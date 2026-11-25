package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class CategoryAlreadyExistExcpetion extends RuntimeException {
    public CategoryAlreadyExistExcpetion() {
        super(MessageUtil.getMessage("category.duplicate"));
    }
}
