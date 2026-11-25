package org.bootcamp.ecommerce.dto.product;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record UpdateProductVariationRequest(
        @NotBlank(message = "{validation.product.variation.id.blank}")
        String id,

        @Min(value = 0, message = "{validation.product.variation.quantity.min}")
        Integer quantityAvailable,

        @Min(value = 0, message = "{validation.product.variation.price.min}")
        @Digits(integer = 9, fraction = 2, message = "{validation.product.variation.price.digits}")
        Double price,

        Map<String, String> metadata,

        Boolean isActive
) {
}