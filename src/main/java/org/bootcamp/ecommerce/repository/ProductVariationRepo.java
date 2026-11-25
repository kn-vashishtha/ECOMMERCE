package org.bootcamp.ecommerce.repository;

import org.bootcamp.ecommerce.dto.category.PriceRange;
import org.bootcamp.ecommerce.entity.ProductVariation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductVariationRepo extends JpaRepository<ProductVariation, UUID>, JpaSpecificationExecutor<ProductVariation> {

    @Query("SELECT MIN(pv.price) AS minPrice, MAX(pv.price) AS maxPrice FROM ProductVariation pv " +
            "JOIN pv.product p WHERE p.category.id IN :categoryIds AND p.isActive = true AND pv.isActive = true")
    PriceRange findMinAndMaxPriceByCategoryIds(@Param("categoryIds") List<UUID> categoryIds);
}