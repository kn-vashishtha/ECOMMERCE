package org.bootcamp.ecommerce.repository;

import org.bootcamp.ecommerce.entity.CategoryMetadataFieldValues;
import org.bootcamp.ecommerce.entity.compositekeys.CategoryMetadataFieldValuesId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryMetadataFieldValuesRepo extends JpaRepository<CategoryMetadataFieldValues, CategoryMetadataFieldValuesId> {
    List<CategoryMetadataFieldValues> findByCategory_Id(UUID id);

    List<CategoryMetadataFieldValues> findByCategory_IdIn(List<UUID> id);
}
