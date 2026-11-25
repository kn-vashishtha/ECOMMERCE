package org.bootcamp.ecommerce.dto.product;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ViewVariationResponse(
        UUID variationId,
        int quantityAvailable,
        double price,
        boolean isActive,
        String primaryImageUrl,
        List<String> secondaryImageUrls,
        Map<String, String> metadata,
        ViewProductResponse parentProduct
) {
}