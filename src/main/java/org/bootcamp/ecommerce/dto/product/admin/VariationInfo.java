package org.bootcamp.ecommerce.dto.product.admin;

import java.util.Map;
import java.util.UUID;

public record VariationInfo(
        UUID variationId,
        int quantityAvailable,
        double price,
        boolean isActive,
        String primaryImageUrl,
        Map<String, String> metadata
) {
}