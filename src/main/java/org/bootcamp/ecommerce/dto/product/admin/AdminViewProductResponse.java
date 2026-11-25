package org.bootcamp.ecommerce.dto.product.admin;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record AdminViewProductResponse(
        UUID productId,
        String productName,
        String description,
        String brand,
        boolean isActive,
        boolean isCancellable,
        boolean isReturnable,
        Map<String, Object> category,
        Map<String, Object> seller,
        List<VariationInfo> variations
) {
}