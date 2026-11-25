package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class DuplicateCompanyNameException extends RuntimeException {
    public DuplicateCompanyNameException() {
        super(MessageUtil.getMessage("company.duplicate"));
    }
}
