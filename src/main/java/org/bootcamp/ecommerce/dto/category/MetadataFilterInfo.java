package org.bootcamp.ecommerce.dto.category;

import java.util.Set;
import java.util.UUID;


public record MetadataFilterInfo(UUID fieldId, String fieldName, Set<String> values) {
}
