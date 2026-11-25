package org.bootcamp.ecommerce.dto.product.customer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CustomerViewProductResponse(
        UUID productId,
        String productName,
        String description,
        String brand,
        boolean isCancellable,
        boolean isReturnable,
        Map<String, Object> category,
        Map<String, Object> seller,
        List<VariationDetail> variations
) {
}