package org.bootcamp.ecommerce.dto.product.customer;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record VariationDetail(
        UUID variationId,
        int quantityAvailable,
        double price,
        String primaryImageUrl,
        List<String> secondaryImageUrls,
        Map<String, String> metadata
) {
}