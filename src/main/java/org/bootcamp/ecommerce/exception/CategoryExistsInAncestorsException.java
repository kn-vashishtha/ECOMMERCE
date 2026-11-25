package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class CategoryExistsInAncestorsException extends RuntimeException {
    public CategoryExistsInAncestorsException() {
        super(MessageUtil.getMessage("category.ancestors.duplicate"));
    }

}
