package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class FileSavingException extends RuntimeException {
    public FileSavingException() {
        super(MessageUtil.getMessage("file.saving.failed"));
    }
}
