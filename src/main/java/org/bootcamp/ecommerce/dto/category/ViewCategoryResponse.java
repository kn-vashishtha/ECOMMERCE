package org.bootcamp.ecommerce.dto.category;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ViewCategoryResponse(
        String id,
        String name,
        Map<String, UUID> ancestorPath,
        Map<String, UUID> subCategories,
        List<MetadataFieldInfo> metadataFields
) {
}
