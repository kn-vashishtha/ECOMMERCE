package org.bootcamp.ecommerce.dto.category;

import java.util.List;
import java.util.Set;


public record CategoryFilterDetailsResponse(List<MetadataFilterInfo> metadataFields, Set<String> brands,
                                            PriceRange priceRange) {
}