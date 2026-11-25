package org.bootcamp.ecommerce.dto.product;

import jakarta.validation.constraints.NotBlank;

public record UpdateProductRequest(
        @NotBlank(message = "{validation.product.id.blank}")
        String id,
        String name, String description,

        Boolean isCancellable, Boolean isReturnable) {
}

