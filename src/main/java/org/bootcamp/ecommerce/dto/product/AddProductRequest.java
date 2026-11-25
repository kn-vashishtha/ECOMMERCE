package org.bootcamp.ecommerce.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddProductRequest(
        @NotBlank(message = "{validation.product.name.blank}")
        String name,
        @Size(min = 5, max = 100, message = "{validation.product.description.size}")
        String description,
        @NotBlank(message = "{validation.product.category.blank}")
        String categoryId,

        @NotBlank(message = "{validation.product.brand.blank}")
        String brand,

        boolean isCancellable,
        boolean isReturnable
) {
}
