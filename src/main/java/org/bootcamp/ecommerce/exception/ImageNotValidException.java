package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class ImageNotValidException extends RuntimeException {
    public ImageNotValidException(String values) {
        super(MessageUtil.getMessagePlaceHolder("image.validation.failed", values));
    }
}
