package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class MetadataCanOnlyBeAddedToLeafCategoryException extends RuntimeException {
    public MetadataCanOnlyBeAddedToLeafCategoryException() {
        super(MessageUtil.getMessage("category.metadata.fieldvalues.leafonly"));
    }
}
