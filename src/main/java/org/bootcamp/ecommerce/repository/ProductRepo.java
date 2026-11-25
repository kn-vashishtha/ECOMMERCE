package org.bootcamp.ecommerce.repository;

import org.bootcamp.ecommerce.entity.Category;
import org.bootcamp.ecommerce.entity.Product;
import org.bootcamp.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ProductRepo extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    boolean existsByNameAndBrandAndCategoryAndSeller(String name, String brand, Category category, User seller);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.id != :productId AND p.isActive = true AND p.variations IS NOT EMPTY")
    Page<Product> findSimilarProducts(@Param("categoryId") UUID categoryId, @Param("productId") UUID productId, Pageable pageable);

    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.category.id IN :categoryIds AND p.isActive = true AND p.variations IS NOT EMPTY")
    Set<String> findDistinctBrandsByCategoryIds(@Param("categoryIds") List<UUID> categoryIds);
}
