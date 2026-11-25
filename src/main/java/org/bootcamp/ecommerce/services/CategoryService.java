package org.bootcamp.ecommerce.services;

import jakarta.validation.Valid;
import org.bootcamp.ecommerce.dto.auth.GetAllResponse;
import org.bootcamp.ecommerce.dto.category.AddCategoryRequest;
import org.bootcamp.ecommerce.dto.category.CategoryFilterDetailsResponse;
import org.bootcamp.ecommerce.dto.category.UpdateCategoryRequest;
import org.bootcamp.ecommerce.dto.category.ViewCategoryResponse;
import org.bootcamp.ecommerce.dto.category.metadata.CategoryMetadataRequest;
import org.bootcamp.ecommerce.dto.category.metadata.MetadataFieldRequest;
import org.bootcamp.ecommerce.entity.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CategoryService {
    ResponseEntity<String> addMetadataField(MetadataFieldRequest metadataFieldRequest);

    ResponseEntity<GetAllResponse<Map<String, UUID>>> getAllMetadataFields(Pageable pageable, String query);

    ResponseEntity<String> addCategory(@Valid AddCategoryRequest category);

    ResponseEntity<ViewCategoryResponse> viewCategory(String id);

    ResponseEntity<GetAllResponse<ViewCategoryResponse>> viewAllCategoryAdmin(Pageable pageable, String name);

    ResponseEntity<String> updateCategory(@Valid UpdateCategoryRequest updateCategoryRequest);

    ResponseEntity<String> addCategoryMetadataFieldValues(CategoryMetadataRequest request);

    ResponseEntity<String> updateCategoryMetadataFieldValues(CategoryMetadataRequest request);

    ResponseEntity<GetAllResponse<ViewCategoryResponse>> viewAllCategorySeller(Pageable pageable, String name);

    ResponseEntity<GetAllResponse<Map<String, Object>>> viewAllCategoryCustomer(Pageable pageable, String parentId);

    void findAllLeafDescendants(Category category, List<Category> leaves);

    ResponseEntity<CategoryFilterDetailsResponse> getCategoryFilterDetails(String categoryId);

}
