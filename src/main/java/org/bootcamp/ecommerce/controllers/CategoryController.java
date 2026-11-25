package org.bootcamp.ecommerce.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bootcamp.ecommerce.dto.auth.GetAllResponse;
import org.bootcamp.ecommerce.dto.category.AddCategoryRequest;
import org.bootcamp.ecommerce.dto.category.CategoryFilterDetailsResponse;
import org.bootcamp.ecommerce.dto.category.UpdateCategoryRequest;
import org.bootcamp.ecommerce.dto.category.ViewCategoryResponse;
import org.bootcamp.ecommerce.dto.category.metadata.CategoryMetadataRequest;
import org.bootcamp.ecommerce.dto.category.metadata.MetadataFieldRequest;
import org.bootcamp.ecommerce.services.CategoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/category-metadata-field/add")
    public ResponseEntity<String> addMetadataField(@Valid @RequestBody MetadataFieldRequest metadataFieldRequest) {
        log.info("addMetadataField called with request: {}", metadataFieldRequest);
        return categoryService.addMetadataField(metadataFieldRequest);
    }

    @GetMapping("/category-metadata-field")
    public ResponseEntity<GetAllResponse<Map<String, UUID>>> getMetadataField(
            @PageableDefault(sort = "name") Pageable pageable,
            @RequestParam(name = "name", required = false, defaultValue = "") String query
    ) {
        log.info("getMetadataField called with query: {}, pageable: {}", query, pageable);
        return categoryService.getAllMetadataFields(pageable, query);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addCategory(@Valid @RequestBody AddCategoryRequest category) {
        log.info("addCategory called with request: {}", category);
        return categoryService.addCategory(category);
    }

    @GetMapping("/view-a-category")
    public ResponseEntity<ViewCategoryResponse> viewCategory(@RequestParam(name = "id") String id) {
        log.info("viewCategory called with id: {}", id);
        return categoryService.viewCategory(id);
    }

    @GetMapping("/view-all-category")
    public ResponseEntity<GetAllResponse<ViewCategoryResponse>> viewAllCategoryAdmin(
            @PageableDefault(sort = "id") Pageable pageable,
            @RequestParam(name = "name", required = false, defaultValue = "") String name
    ) {
        log.info("viewAllCategoryAdmin called with name: {}, pageable: {}", name, pageable);
        return categoryService.viewAllCategoryAdmin(pageable, name);
    }

    @PutMapping("/update-category")
    public ResponseEntity<String> updateCategory(@Valid @RequestBody UpdateCategoryRequest updateCategoryRequest) {
        log.info("updateCategory called with request: {}", updateCategoryRequest);
        return categoryService.updateCategory(updateCategoryRequest);
    }

    @PostMapping("/category-metadata-field-values/add")
    public ResponseEntity<String> categoryMetadataFieldValuesAdd(@Valid @RequestBody CategoryMetadataRequest request) {
        log.info("categoryMetadataFieldValuesAdd called with request: {}", request);
        return categoryService.addCategoryMetadataFieldValues(request);
    }

    @PutMapping("/category-metadata-field-values/update")
    public ResponseEntity<String> categoryMetadataFieldValuesUpdate(@Valid @RequestBody CategoryMetadataRequest request) {
        log.info("categoryMetadataFieldValuesUpdate called with request: {}", request);
        return categoryService.updateCategoryMetadataFieldValues(request);
    }

    @GetMapping("/seller/view-all-category")
    public ResponseEntity<GetAllResponse<ViewCategoryResponse>> viewCategorySeller(
            @PageableDefault(sort = "id") Pageable pageable,
            @RequestParam(name = "name", required = false, defaultValue = "") String name
    ) {
        log.info("viewAllCategorySeller called with name: {}, pageable: {}", name, pageable);
        return categoryService.viewAllCategorySeller(pageable, name);
    }

    @GetMapping("/customer/view-all-category")
    public ResponseEntity<GetAllResponse<Map<String, Object>>> viewAllCategoryCustomer(
            @PageableDefault(sort = "id") Pageable pageable,
            @RequestParam(name = "parent_id", required = false) String parentId
    ) {
        log.info("viewAllCategoryCustomer called with parentId: {}, pageable: {}", parentId, pageable);
        return categoryService.viewAllCategoryCustomer(pageable, parentId);
    }

    @GetMapping("/customer/filters")
    public ResponseEntity<CategoryFilterDetailsResponse> getCategoryFilters(@RequestParam("id") String id) {
        log.info("getCategoryFilters called with id: {}", id);
        return categoryService.getCategoryFilterDetails(id);
    }

}
