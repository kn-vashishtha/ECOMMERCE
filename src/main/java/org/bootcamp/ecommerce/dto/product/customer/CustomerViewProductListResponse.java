package org.bootcamp.ecommerce.dto.product.customer;

import java.util.UUID;

public record CustomerViewProductListResponse(
        UUID productId,
        String productName,
        String brand,
        String primaryImageUrl
) {
}