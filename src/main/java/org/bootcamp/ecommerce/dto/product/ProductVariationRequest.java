package org.bootcamp.ecommerce.dto.product;

import jakarta.validation.constraints.*;

import java.util.Map;

public record ProductVariationRequest(
        @NotBlank(message = "{validation.product.variation.id.blank}")
        String productId,

        @NotNull(message = "{validation.product.variation.quantity.required}")
        @Min(value = 0, message = "{validation.product.variation.quantity.min}")
        Integer quantityAvailable,

        @NotNull(message = "{validation.product.variation.price}")
        @Min(value = 0, message = "{validation.product.variation.price.min}")
        @Digits(integer = 9, fraction = 2, message = "{validation.product.variation.price.digits}")
        Double price,

        @NotEmpty(message = "{validation.product.variation.metadata.notempty}")
        Map<String, String> metadata
) {
}