package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class DuplicateGstException extends RuntimeException {
    public DuplicateGstException() {
        super(MessageUtil.getMessage("gst.duplicate"));
    }
}
