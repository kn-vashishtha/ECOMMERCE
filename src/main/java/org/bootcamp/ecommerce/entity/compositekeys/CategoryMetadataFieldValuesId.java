package org.bootcamp.ecommerce.entity.compositekeys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMetadataFieldValuesId implements Serializable {
    @Column(name = "category_metadata_field_id")
    private UUID categoryMetadataFieldId;

    @Column(name = "category_id")
    private UUID categoryId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CategoryMetadataFieldValuesId that = (CategoryMetadataFieldValuesId) o;
        return Objects.equals(categoryMetadataFieldId, that.categoryMetadataFieldId) && Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryMetadataFieldId, categoryId);
    }
}
