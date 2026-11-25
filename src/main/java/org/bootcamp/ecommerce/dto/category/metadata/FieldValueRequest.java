package org.bootcamp.ecommerce.dto.category.metadata;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FieldValueRequest(
        @NotNull(message = "{category.metadata.fieldid.required}")
        @NotBlank(message = "{category.metadata.fieldid.blank}")
        String fieldId,
        @NotNull(message = "{category.fieldvalues.required}")
        @NotBlank(message = "{category.fieldvalues.blank}")
        String values
) {
}
