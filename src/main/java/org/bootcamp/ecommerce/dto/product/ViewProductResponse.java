package org.bootcamp.ecommerce.dto.product;

import java.util.Map;
import java.util.UUID;

public record ViewProductResponse(
        UUID id,
        String name,
        String description,
        String brand,
        boolean isCancellable,
        boolean isReturnable,
        boolean isActive,
        Map<String, Object> category
) {
}