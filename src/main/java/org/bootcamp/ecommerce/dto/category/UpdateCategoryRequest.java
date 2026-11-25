package org.bootcamp.ecommerce.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCategoryRequest(
        @NotNull(message = "{validation.category.id.required}")
        @NotBlank(message = "{validation.category.id.blank}")
        String id,
        @NotNull(message = "{validation.category.required}")
        @NotBlank(message = "{validation.category.blank}") String newName
) {
}
