package org.bootcamp.ecommerce.dto.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddCategoryRequest(
        @NotNull(message = "{validation.category.required}")
        @NotBlank(message = "{validation.category.blank}")
        String name,
        @JsonProperty("parent_category_id")
        String parentCategoryId
) {
}
