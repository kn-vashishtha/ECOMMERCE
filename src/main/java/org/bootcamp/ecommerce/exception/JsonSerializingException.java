package org.bootcamp.ecommerce.exception;

import org.bootcamp.ecommerce.utils.MessageUtil;

public class JsonSerializingException extends RuntimeException {
    public JsonSerializingException() {
        super(MessageUtil.getMessage("error.json.parse"));
    }

}
