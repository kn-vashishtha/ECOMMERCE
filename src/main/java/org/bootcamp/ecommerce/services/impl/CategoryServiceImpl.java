package org.bootcamp.ecommerce.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bootcamp.ecommerce.constants.Consts;
import org.bootcamp.ecommerce.dto.auth.GetAllResponse;
import org.bootcamp.ecommerce.dto.category.*;
import org.bootcamp.ecommerce.dto.category.metadata.CategoryMetadataRequest;
import org.bootcamp.ecommerce.dto.category.metadata.FieldValueRequest;
import org.bootcamp.ecommerce.dto.category.metadata.MetadataFieldRequest;
import org.bootcamp.ecommerce.entity.Category;
import org.bootcamp.ecommerce.entity.CategoryMetaDataField;
import org.bootcamp.ecommerce.entity.CategoryMetadataFieldValues;
import org.bootcamp.ecommerce.entity.compositekeys.CategoryMetadataFieldValuesId;
import org.bootcamp.ecommerce.exception.*;
import org.bootcamp.ecommerce.repository.*;
import org.bootcamp.ecommerce.services.CategoryService;
import org.bootcamp.ecommerce.utils.MessageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMetadataFieldRepo categoryMetadataFieldRepo;
    private final CategoryRepo categoryRepo;
    private final CategoryMetadataFieldValuesRepo categoryMetadataFieldValuesRepo;
    private final ProductRepo productRepo;
    private final ProductVariationRepo productVariationRepo;

    private static CategoryMetadataFieldValues buildCategoryMetadataFieldValues(CategoryMetadataFieldValuesId id, Category category, CategoryMetaDataField field, String uniqueValues) {
        CategoryMetadataFieldValues newValues = new CategoryMetadataFieldValues();
        newValues.setCategoryMetadataFieldValuesId(id);
        newValues.setCategory(category);
        newValues.setCategoryMetaDataField(field);
        newValues.setValues(uniqueValues);
        return newValues;
    }

    @Override
    public ResponseEntity<String> addMetadataField(MetadataFieldRequest request) {
        log.debug("Admin adding metadata field with name: {}", request.name());
        if (categoryMetadataFieldRepo.existsByNameIgnoreCase(request.name())) {
            log.warn("Attempted to add a duplicate metadata field: {}", request.name());
            throw new CategoryMetaDataFieldAlreadyExistsException(request.name());
        }
        CategoryMetaDataField categoryMetaDataField = CategoryMetaDataField.builder().name(request.name()).build();
        categoryMetadataFieldRepo.save(categoryMetaDataField);
        log.info("Successfully added metadata field: {}", request.name());
        return ResponseEntity.ok(MessageUtil.getMessage("category.metadata.field.added"));
    }

    @Override
    public ResponseEntity<GetAllResponse<Map<String, UUID>>> getAllMetadataFields(Pageable pageable, String query) {
        log.debug("Admin getting all metadata fields with query: '{}'", query);
        Page<CategoryMetaDataField> page = categoryMetadataFieldRepo.findByNameContainingIgnoreCase(query, pageable);
        Map<String, UUID> nameIdMap = page.getContent().stream().collect(Collectors.toMap(CategoryMetaDataField::getName, CategoryMetaDataField::getId, (existing, replacement) -> existing));
        GetAllResponse<Map<String, UUID>> response = GetAllResponse.<Map<String, UUID>>builder().data(List.of(nameIdMap)).pageNumber(page.getNumber()).pageSize(page.getSize()).totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).lastPage(page.isLast()).build();
        log.debug("Found {} metadata fields.", page.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional
    public ResponseEntity<String> addCategory(AddCategoryRequest request) {
        log.debug("Admin adding category with name: '{}' and parent ID: '{}'", request.name(), request.parentCategoryId());
        Category category = new Category();
        category.setName(request.name());
        Category savedCategory;
        if (request.parentCategoryId() != null) {
            Category parent = categoryRepo.findById(UUID.fromString(request.parentCategoryId())).orElseThrow(InvalidParentCategoryException::new);
            validateCategoryNotInAncestors(request.name(), parent);
            validateCategoryUniqueness(request.name(), parent);
            category.setParentCategory(parent);
            savedCategory = categoryRepo.save(category);
            parent.getSubCategories().add(savedCategory);
            categoryRepo.save(parent);
        } else {
            validateCategoryUniqueness(request.name(), null);
            savedCategory = categoryRepo.save(category);
        }
        log.info("Successfully added category with ID: {}", savedCategory.getId());
        return ResponseEntity.ok(MessageUtil.getMessage("category.added.success") + savedCategory.getId());
    }

    @Override
    public ResponseEntity<ViewCategoryResponse> viewCategory(String id) {
        log.debug("Admin viewing category with ID: {}", id);
        Category category = getCategoryById(UUID.fromString(id));
        return ResponseEntity.ok(buildViewCategoryResponse(category));
    }

    @Override
    public ResponseEntity<GetAllResponse<ViewCategoryResponse>> viewAllCategoryAdmin(Pageable pageable, String name) {
        log.debug("Admin viewing all categories with name filter: '{}'", name);
        Page<Category> page = categoryRepo.findByNameContainingIgnoreCase(name, pageable);
        log.debug("Found {} categories matching filter.", page.getTotalElements());
        return buildPaginatedResponse(page);
    }

    @Override
    public ResponseEntity<String> updateCategory(UpdateCategoryRequest request) {
        log.debug("Admin updating category with ID: {}", request.id());
        Category categoryToUpdate = getCategoryById(UUID.fromString(request.id()));
        String newName = request.newName().trim();

        if (categoryToUpdate.getName().equalsIgnoreCase(newName)) {
            log.debug("Category name is unchanged. No update needed.");
            return ResponseEntity.ok(MessageUtil.getMessage("category.update.nochange"));
        }
        validateCategoryNotInAncestors(newName, categoryToUpdate.getParentCategory());
        validateCategoryUniqueness(newName, categoryToUpdate.getParentCategory());
        categoryToUpdate.setName(newName);
        categoryRepo.save(categoryToUpdate);
        log.info("Successfully updated category ID {} with new name '{}'", categoryToUpdate.getId(), newName);
        return ResponseEntity.ok(MessageUtil.getMessagePlaceHolder("category.updated.success", categoryToUpdate.getId()));
    }

    @Override
    @Transactional
    public ResponseEntity<String> addCategoryMetadataFieldValues(CategoryMetadataRequest request) {
        log.debug("Admin adding metadata values for category ID: {}", request.categoryId());

        Category category = getCategoryById(UUID.fromString(request.categoryId()));
        if (!category.isLeaf()) {
            log.warn("Attempted to add metadata to a non-leaf category: {}", request.categoryId());
            throw new MetadataCanOnlyBeAddedToLeafCategoryException();
        }
        for (FieldValueRequest fieldValueRequest : request.fieldValues()) {
            CategoryMetaDataField field = getMetadataFieldById(UUID.fromString(fieldValueRequest.fieldId()));
            String uniqueValues = validateAndFormatUniqueValues(fieldValueRequest.values());
            CategoryMetadataFieldValuesId id = new CategoryMetadataFieldValuesId(UUID.fromString(fieldValueRequest.fieldId()), UUID.fromString(request.categoryId()));
            if (categoryMetadataFieldValuesRepo.existsById(id)) {
                log.warn("Metadata field values already exist for category {} and field {}", category.getId(), field.getId());
                throw new CategoryMetadataFieldValuesAlreadyExistsException(fieldValueRequest.fieldId(), fieldValueRequest.values());
            }
            CategoryMetadataFieldValues newValues = buildCategoryMetadataFieldValues(id, category, field, uniqueValues);
            categoryMetadataFieldValuesRepo.save(newValues);
            log.debug("Saved metadata values for field '{}' to category '{}'", field.getName(), category.getName());
        }
        log.info("Successfully added all metadata values for category ID: {}", request.categoryId());
        return ResponseEntity.ok(MessageUtil.getMessage("category.metadata.fieldvalues.added"));
    }

    @Override
    @Transactional
    public ResponseEntity<String> updateCategoryMetadataFieldValues(CategoryMetadataRequest request) {
        log.debug("Admin updating metadata values for category ID: {}", request.categoryId());
        Category category = getCategoryById(UUID.fromString(request.categoryId()));
        for (FieldValueRequest fieldValueRequest : request.fieldValues()) {
            CategoryMetadataFieldValuesId id = new CategoryMetadataFieldValuesId(UUID.fromString(fieldValueRequest.fieldId()), UUID.fromString(request.categoryId()));
            CategoryMetadataFieldValues valuesToUpdate = categoryMetadataFieldValuesRepo.findById(id).orElseThrow(() -> new CategoryMetaDataFieldValuesNotFoundException(request.categoryId(), fieldValueRequest.fieldId()));
            Set<String> allValues = new LinkedHashSet<>();
            String existingValues = valuesToUpdate.getValues();
            if (existingValues != null && !existingValues.isBlank()) {
                allValues.addAll(Arrays.asList(existingValues.split(Consts.FIELDVALUESREGEX)));
            }
            String newValuesFromRequest = fieldValueRequest.values().toUpperCase();
            String[] newValues = newValuesFromRequest.split(Consts.FIELDVALUESREGEX);
            for (String newValue : newValues) {
                if (allValues.contains(newValue.trim())) {
                    log.warn("Attempted to add a duplicate metadata value '{}' to field {}", newValue, fieldValueRequest.fieldId());
                    throw new DuplicateFieldValuesException(newValue.trim());
                }
            }
            allValues.addAll(Arrays.asList(newValues));
            valuesToUpdate.setValues(String.join(", ", allValues));
            log.debug("Updated metadata for field '{}' in category '{}'", fieldValueRequest.fieldId(), category.getId());
        }
        log.info("Successfully updated all metadata values for category ID: {}", request.categoryId());
        return ResponseEntity.ok(MessageUtil.getMessage("category.metadata.fieldvalues.updated"));
    }

    @Override
    public ResponseEntity<GetAllResponse<ViewCategoryResponse>> viewAllCategorySeller(Pageable pageable, String name) {
        log.debug("Seller viewing leaf categories with name filter: '{}'", name);
        Page<Category> page = categoryRepo.findLeafCategoriesByNameContainingIgnoreCase(name, pageable);
        log.debug("Found {} leaf categories for seller.", page.getTotalElements());
        return buildPaginatedResponse(page);

    }

    @Override
    public ResponseEntity<GetAllResponse<Map<String, Object>>> viewAllCategoryCustomer(Pageable pageable, String parentId) {
        log.debug("Customer viewing categories with parent ID: '{}'", parentId);
        Page<Category> page;
        if (parentId == null || parentId.isBlank()) {
            page = categoryRepo.findAllByParentCategoryIsNull(pageable);
        } else {
            page = categoryRepo.findByParentCategoryId(UUID.fromString(parentId), pageable);
        }
        log.debug("Found {} categories for customer.", page.getTotalElements());
        List<Map<String, Object>> dataList = page.getContent().stream().map(category -> {
            Map<String, Object> categoryMap = new LinkedHashMap<>();
            categoryMap.put("id", category.getId());
            categoryMap.put("name", category.getName());
            return categoryMap;
        }).toList();
        GetAllResponse<Map<String, Object>> response = GetAllResponse.<Map<String, Object>>builder().data(dataList).pageNumber(page.getNumber()).pageSize(page.getSize()).totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).lastPage(page.isLast()).build();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CategoryFilterDetailsResponse> getCategoryFilterDetails(String categoryId) {
        log.debug("Customer fetching filter details for category ID: {}", categoryId);
        Category category = categoryRepo.findById(UUID.fromString(categoryId)).orElseThrow(() -> new CategoryNotFoundException(categoryId));
        List<UUID> relevantCategoryIds;
        if (category.isLeaf()) {
            relevantCategoryIds = List.of(category.getId());
        } else {
            List<Category> leafDescendants = new ArrayList<>();
            findAllLeafDescendants(category, leafDescendants);
            relevantCategoryIds = leafDescendants.stream().map(Category::getId).toList();
        }
        log.debug("Found {} relevant leaf category IDs for filter aggregation.", relevantCategoryIds.size());

        if (relevantCategoryIds.isEmpty()) {
            return ResponseEntity.ok(new CategoryFilterDetailsResponse(Collections.emptyList(), Collections.emptySet(), new PriceRange(null, null)));
        }

        List<MetadataFilterInfo> metadata = getMetadataForCategories(relevantCategoryIds);
        Set<String> brands = productRepo.findDistinctBrandsByCategoryIds(relevantCategoryIds);
        PriceRange priceRangeResult = productVariationRepo.findMinAndMaxPriceByCategoryIds(relevantCategoryIds);

        CategoryFilterDetailsResponse response = new CategoryFilterDetailsResponse(metadata, brands, priceRangeResult);
        log.debug("Successfully aggregated filter details for category ID: {}", categoryId);
        return ResponseEntity.ok(response);
    }

    @Transactional
    public void findAllLeafDescendants(Category category, List<Category> leaves) {
        if (category.getSubCategories().isEmpty()) {
            leaves.add(category);
        } else {
            for (Category subCategory : category.getSubCategories()) {
                findAllLeafDescendants(subCategory, leaves);
            }
        }
    }

    private Map<String, UUID> getAncestorPath(Category category) {
        List<Map.Entry<String, UUID>> entries = new ArrayList<>();
        Category current = category.getParentCategory();
        while (current != null) {
            entries.add(Map.entry(current.getName(), current.getId()));
            current = current.getParentCategory();
        }
        Collections.reverse(entries);
        Map<String, UUID> ancestorPath = new LinkedHashMap<>();
        for (Map.Entry<String, UUID> entry : entries) {
            ancestorPath.put(entry.getKey(), entry.getValue());
        }
        return ancestorPath;
    }

    private void validateCategoryNotInAncestors(String name, Category parent) {
        log.debug("Validating that '{}' does not exist in ancestors of parent: {}", name, parent != null ? parent.getName() : "root");
        Category current = parent;
        while (current != null) {
            if (current.getName().equalsIgnoreCase(name)) {
                log.warn("Validation failed: Category name '{}' exists in ancestors.", name);
                throw new CategoryExistsInAncestorsException();
            }
            current = current.getParentCategory();
        }
    }

    private ViewCategoryResponse buildViewCategoryResponse(Category category) {
        Map<String, UUID> ancestorPath = getAncestorPath(category);
        Map<String, UUID> subCategoryMap = category.getSubCategories().stream().collect(Collectors.toMap(Category::getName, Category::getId, (id1, id2) -> id1, LinkedHashMap::new));
        List<MetadataFieldInfo> metadataFields;
        if (!category.getSubCategories().isEmpty()) {
            metadataFields = calculateUnionMetadata(category);
        } else {
            metadataFields = categoryMetadataFieldValuesRepo.findByCategory_Id(category.getId()).stream().map(fieldValue -> {
                String valuesString = fieldValue.getValues();
                List<String> valuesList = (valuesString == null || valuesString.isBlank()) ? Collections.emptyList() : Arrays.asList(valuesString.split(Consts.FIELDVALUESREGEX));
                return new MetadataFieldInfo(fieldValue.getCategoryMetaDataField().getId(), fieldValue.getCategoryMetaDataField().getName(), valuesList);
            }).toList();
        }
        return new ViewCategoryResponse(category.getId().toString(), category.getName(), ancestorPath, subCategoryMap, metadataFields);
    }

    private List<MetadataFieldInfo> calculateUnionMetadata(Category category) {
        List<Category> leafDescendants = new ArrayList<>();
        findAllLeafDescendants(category, leafDescendants);
        if (leafDescendants.isEmpty()) {
            return Collections.emptyList();
        }
        Map<UUID, Set<String>> unionValuesMap = new HashMap<>();
        Map<UUID, String> idToNameMap = new HashMap<>();
        for (Category leaf : leafDescendants) {
            List<CategoryMetadataFieldValues> leafMetadata = categoryMetadataFieldValuesRepo.findByCategory_Id(leaf.getId());
            for (CategoryMetadataFieldValues cmfv : leafMetadata) {
                UUID fieldId = cmfv.getCategoryMetaDataField().getId();
                String fieldName = cmfv.getCategoryMetaDataField().getName();
                Set<String> values = new HashSet<>(Arrays.asList(cmfv.getValues().split(Consts.FIELDVALUESREGEX)));
                idToNameMap.putIfAbsent(fieldId, fieldName);
                unionValuesMap.computeIfAbsent(fieldId, k -> new HashSet<>()).addAll(values);
            }
        }
        return unionValuesMap.entrySet().stream().map(entry -> new MetadataFieldInfo(entry.getKey(), idToNameMap.get(entry.getKey()), new ArrayList<>(entry.getValue()))).toList();
    }

    private String validateAndFormatUniqueValues(String values) {
        log.debug("Validating and formatting unique values from string: '{}'", values);
        String[] valueArray = values.toUpperCase().split(Consts.FIELDVALUESREGEX);
        Set<String> uniqueValues = new LinkedHashSet<>(Arrays.asList(valueArray));
        if (uniqueValues.size() != valueArray.length) {
            log.warn("Duplicate values found in string: '{}'", values);
            throw new DuplicateFieldValuesException(values);
        }
        return String.join(",", uniqueValues);
    }

    private List<MetadataFilterInfo> getMetadataForCategories(List<UUID> categoryIds) {
        List<CategoryMetadataFieldValues> allFields = categoryMetadataFieldValuesRepo.findByCategory_IdIn(categoryIds);
        Map<UUID, List<CategoryMetadataFieldValues>> fieldsById = allFields.stream().collect(Collectors.groupingBy(field -> field.getCategoryMetaDataField().getId()));
        return fieldsById.values().stream().map(fieldList -> {
            CategoryMetadataFieldValues firstField = fieldList.get(0);
            Set<String> allValues = fieldList.stream().flatMap(field -> Arrays.stream(field.getValues().split(Consts.FIELDVALUESREGEX))).collect(Collectors.toSet());

            return new MetadataFilterInfo(firstField.getCategoryMetaDataField().getId(), firstField.getCategoryMetaDataField().getName(), allValues);
        }).toList();
    }

    private ResponseEntity<GetAllResponse<ViewCategoryResponse>> buildPaginatedResponse(Page<Category> page) {
        List<ViewCategoryResponse> data = page.getContent().stream().map(this::buildViewCategoryResponse).toList();
        GetAllResponse<ViewCategoryResponse> response = GetAllResponse.<ViewCategoryResponse>builder().data(data).pageNumber(page.getNumber()).pageSize(page.getSize()).totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).build();
        return ResponseEntity.ok(response);
    }

    private CategoryMetaDataField getMetadataFieldById(UUID id) {
        log.debug("Fetching metadata field with ID: {}", id);
        return categoryMetadataFieldRepo.findById(id).orElseThrow(() -> new CategoryMetaDataFieldNotFoundException(id.toString()));
    }

    private Category getCategoryById(UUID id) {
        log.debug("Fetching category with ID: {}", id);
        return categoryRepo.findById(id).orElseThrow(() -> new CategoryNotFoundException(id.toString()));
    }

    private void validateCategoryUniqueness(String name, Category parent) {
        log.debug("Validating uniqueness of category name '{}' under parent: {}", name, parent != null ? parent.getName() : "root");
        boolean exists;
        if (parent != null) {
            exists = categoryRepo.existsByNameAndParentCategory(name, parent);
        } else {
            exists = categoryRepo.existsByNameAndParentCategoryIsNull(name);
        }
        if (exists) {
            log.warn("Validation failed: Category '{}' already exists at this level.", name);
            throw new CategoryAlreadyExistExcpetion();
        }
    }

}
