package org.bootcamp.ecommerce.dto.category.metadata;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CategoryMetadataRequest(
        @NotNull(message = "{validation.category.id.required}")
        @NotBlank(message = "{validation.category.id.blank}")
        String categoryId,
        @Valid
        @NotEmpty(message = "{validation.fieldvalues.onevalue}")
        List<FieldValueRequest> fieldValues
) {
}
