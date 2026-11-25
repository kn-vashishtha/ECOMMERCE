package org.bootcamp.ecommerce.repository;

import org.bootcamp.ecommerce.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CategoryRepo extends JpaRepository<Category, UUID> {
    boolean existsByNameAndParentCategory(String name, Category parentCategory);

    boolean existsByNameAndParentCategoryIsNull(String name);

    Page<Category> findByParentCategoryId(UUID parentCategoryId, Pageable pageable);

    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.name LIKE %:name% AND c.subCategories IS EMPTY")
    Page<Category> findLeafCategoriesByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    Page<Category> findAllByParentCategoryIsNull(Pageable pageable);

}
