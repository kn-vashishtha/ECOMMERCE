package org.bootcamp.ecommerce.dto.category;

import java.util.List;
import java.util.UUID;

public record MetadataFieldInfo(UUID fieldId, String fieldName, List<String> values) {
}
