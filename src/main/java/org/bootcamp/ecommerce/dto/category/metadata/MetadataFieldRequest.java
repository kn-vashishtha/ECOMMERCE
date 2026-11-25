package org.bootcamp.ecommerce.dto.category.metadata;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MetadataFieldRequest(
        @NotNull(message = "{validation.category.metadata.required}")
        @NotBlank(message = "{validation.category.metadata.blank}")
        String name
) {
    public MetadataFieldRequest(String name) {
        this.name = name.trim();
    }
}
