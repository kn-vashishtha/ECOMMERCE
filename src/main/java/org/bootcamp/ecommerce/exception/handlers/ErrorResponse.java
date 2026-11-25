package org.bootcamp.ecommerce.exception.handlers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private int status;
    private List<String> errors;

    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
        this.errors = errors != null ? errors : Collections.emptyList();
    }
}