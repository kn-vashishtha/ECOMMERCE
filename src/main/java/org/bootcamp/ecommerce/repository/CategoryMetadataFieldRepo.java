package org.bootcamp.ecommerce.repository;

import org.bootcamp.ecommerce.entity.CategoryMetaDataField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryMetadataFieldRepo extends JpaRepository<CategoryMetaDataField, UUID> {
    boolean existsByNameIgnoreCase(String name);

    Page<CategoryMetaDataField> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByName(String name);
}
