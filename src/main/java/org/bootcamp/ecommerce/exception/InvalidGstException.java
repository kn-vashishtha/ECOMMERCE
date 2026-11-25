package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class InvalidGstException extends RuntimeException {
    public InvalidGstException() {
        super(MessageUtil.getMessage("gst.invalid"));
    }
}
