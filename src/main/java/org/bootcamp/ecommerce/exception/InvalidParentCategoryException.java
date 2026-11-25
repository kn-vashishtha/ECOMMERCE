package org.bootcamp.ecommerce.exception;


import org.bootcamp.ecommerce.utils.MessageUtil;

public class InvalidParentCategoryException extends RuntimeException {
    public InvalidParentCategoryException() {
        super(MessageUtil.getMessage("{category.parent.invalid}"));
    }
}
