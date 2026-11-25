package org.bootcamp.ecommerce.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bootcamp.ecommerce.constants.Consts;
import org.bootcamp.ecommerce.dto.auth.GetAllResponse;
import org.bootcamp.ecommerce.dto.product.*;
import org.bootcamp.ecommerce.dto.product.admin.AdminViewProductResponse;
import org.bootcamp.ecommerce.dto.product.admin.VariationInfo;
import org.bootcamp.ecommerce.dto.product.customer.CustomerViewProductListResponse;
import org.bootcamp.ecommerce.dto.product.customer.CustomerViewProductResponse;
import org.bootcamp.ecommerce.dto.product.customer.VariationDetail;
import org.bootcamp.ecommerce.entity.*;
import org.bootcamp.ecommerce.exception.*;
import org.bootcamp.ecommerce.processors.AsyncEmailProcessor;
import org.bootcamp.ecommerce.repository.*;
import org.bootcamp.ecommerce.services.CategoryService;
import org.bootcamp.ecommerce.services.FileService;
import org.bootcamp.ecommerce.services.ProductService;
import org.bootcamp.ecommerce.utils.MessageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.bootcamp.ecommerce.utils.SpecificationUtil.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final UserRepo userRepo;
    private final CategoryService categoryService;
    private final ProductVariationRepo productVariationRepo;
    private final AsyncEmailProcessor asyncEmailProcessor;
    private final FileService fileService;
    private final CategoryMetadataFieldValuesRepo categoryMetadataFieldValuesRepo;
    private final ObjectMapper objectMapper;

    @Override
    public ResponseEntity<String> addProduct(AddProductRequest addProductRequest) {
        log.debug("Attempting to add a new product with name: {}", addProductRequest.name());
        final Seller seller = getAuthenticatedSeller();
        final Category category = getAndValidateCategoryIsLeaf(addProductRequest.categoryId());
        if (productRepo.existsByNameAndBrandAndCategoryAndSeller(addProductRequest.name().trim(), addProductRequest.brand().trim(), category, seller)) {
            throw new ProductAlreadyExistsException();
        }
        Product product = Product.builder().name(addProductRequest.name().trim()).description(addProductRequest.description()).brand(addProductRequest.brand().trim()).isCancellable(addProductRequest.isCancellable()).isReturnable(addProductRequest.isReturnable()).category(category).seller(seller).build();
        Product savedProduct = productRepo.save(product);
        log.info("Successfully saved new product with ID: {}", savedProduct.getId());
        asyncEmailProcessor.sendActivationEmailToAdmin(savedProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(MessageUtil.getMessagePlaceHolder("product.added.success", savedProduct.getId().toString()));
    }

    @Override
    @Transactional
    public ResponseEntity<String> addProductVariation(ProductVariationRequest productVariationRequest, MultipartFile primaryImage, List<MultipartFile> secondaryImages) throws IOException {
        log.debug("Attempting to add variation for product ID: {}", productVariationRequest.productId());
        Product product = getProductAndVerifyOwnership(productVariationRequest.productId());
        if (!product.isActive()) {
            log.warn("Attempted to add variation to inactive product ID: {}", productVariationRequest.productId());
            throw new ProductIsNotActiveException(productVariationRequest.productId());
        }
        validateImages(primaryImage, secondaryImages);
        validateVariationMetadata(product.getCategory(), productVariationRequest.metadata());
        ProductVariation variation = createAndSaveInitialVariation(product, productVariationRequest);
        String savedImageName = fileService.uploadProductVariationImage(primaryImage, productVariationRequest.productId(), variation);
        variation.setPrimaryImageName(savedImageName);
        log.debug("Primary image name set to: {}", savedImageName);
        fileService.uploadSecondaryVariationImages(secondaryImages, productVariationRequest.productId(), variation);
        productVariationRepo.save(variation);
        log.info("Successfully added new variation with ID {} to product ID {}", variation.getId(), product.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(MessageUtil.getMessage("product.variation.added.success"));
    }

    @Override
    public ResponseEntity<ViewProductResponse> viewProduct(String productId) {
        log.debug("Attempting to view product with ID: {}", productId);
        Product product = getProductAndVerifyOwnership(productId);
        return ResponseEntity.ok(mapProductToViewResponse(product));
    }

    @Override
    public ResponseEntity<ViewVariationResponse> viewProductVariation(String id) {
        log.debug("Attempting to view product variation with ID: {}", id);
        ProductVariation variation = getVariationAndVerifyOwnership(id);
        return ResponseEntity.ok(mapVariationToViewResponse(variation));
    }

    @Override
    public ResponseEntity<GetAllResponse<ViewProductResponse>> viewAllProducts(String query, String categoryId, Pageable pageable) {
        log.debug("Seller viewing all products with query: '{}', categoryId: '{}'", query, categoryId);
        final Seller seller = getAuthenticatedSeller();
        Specification<Product> spec = bySeller(seller);
        if (query != null && !query.isBlank()) {
            spec = spec.and(nameOrBrandContains(query));
        }
        if (categoryId != null && !categoryId.isBlank()) {
            spec = spec.and(inCategory(UUID.fromString(categoryId)));
        }

        Page<Product> page = productRepo.findAll(spec, pageable);
        log.debug("Found {} products for seller {}", page.getTotalElements(), seller.getEmail());
        return buildPaginatedResponseForProduct(page);
    }

    @Override
    public ResponseEntity<GetAllResponse<ViewVariationResponse>> viewAllProductVariations(String query, String productId, Boolean isActive, Pageable pageable) {
        log.debug("Seller viewing all variations for product ID: {}", productId);
        Product product = getProductAndVerifyOwnership(productId);
        Specification<ProductVariation> specification = forProduct(product);
        if (query != null && !query.isBlank()) {
            specification = specification.and(metadataContains(query));
        }
        if (isActive != null) {
            specification = specification.and(isActiveProductVariation(isActive));
        }
        Page<ProductVariation> page = productVariationRepo.findAll(specification, pageable);
        log.debug("Found {} variations for product {}", page.getTotalElements(), productId);
        return buildPaginatedResponseProductVariations(page);
    }

    @Override
    public ResponseEntity<String> deleteProduct(String productId) {
        log.debug("Attempting to delete product with ID: {}", productId);
        Product product = getProductAndVerifyOwnership(productId);
        productRepo.delete(product);
        log.info("Successfully deleted product with ID: {}", productId);
        return ResponseEntity.ok(MessageUtil.getMessagePlaceHolder("product.deleted", productId));
    }

    @Override
    public ResponseEntity<String> updateProduct(UpdateProductRequest request) {
        log.debug("Attempting to update product with ID: {}", request.id());
        final Seller seller = getAuthenticatedSeller();
        Product productToUpdate = getProductAndVerifyOwnership(request.id());
        String newName = request.name();
        if (productRepo.existsByNameAndBrandAndCategoryAndSeller(newName.trim(), productToUpdate.getBrand(), productToUpdate.getCategory(), seller)) {
            throw new ProductAlreadyExistsException();
        }
        setterProductUpdate(request, productToUpdate);
        productRepo.save(productToUpdate);
        log.info("Successfully updated product with ID: {}", request.id());
        return ResponseEntity.ok(MessageUtil.getMessagePlaceHolder("product.updated.success", request.id()));
    }

    @Override
    @Transactional
    public ResponseEntity<String> updateProductVariation(UpdateProductVariationRequest request, MultipartFile primaryImage, List<MultipartFile> secondaryImages) throws IOException {
        log.debug("Attempting to update variation with ID: {}", request.id());
        ProductVariation variationToUpdate = getVariationAndVerifyOwnership(request.id());
        Product product = variationToUpdate.getProduct();
        if (!product.isActive()) {
            log.warn("Attempted to update variation on inactive product ID: {}", product.getId());
            throw new ProductIsNotActiveException(product.getId().toString());
        }
        updateVariationFields(variationToUpdate, request);
        updateVariationImages(variationToUpdate, primaryImage, secondaryImages);
        productVariationRepo.save(variationToUpdate);
        log.info("Successfully updated variation with ID: {}", request.id());
        return ResponseEntity.ok(MessageUtil.getMessagePlaceHolder("product.variation.updated.success", request.id()));
    }

    @Override
    public ResponseEntity<CustomerViewProductResponse> viewProductCustomer(String productId) {
        log.debug("Customer viewing product with ID: {}", productId);
        Product product = getProductForCustomerViewById(UUID.fromString(productId));
        CustomerViewProductResponse response = mapProductToCustomerViewResponse(product);
        return ResponseEntity.ok(response);

    }

    private ResponseEntity<GetAllResponse<CustomerViewProductListResponse>> buildPaginatedResponseProductCustomer(Page<Product> page) {
        List<CustomerViewProductListResponse> data = page.getContent().stream().map(this::mapProductToCustomerListResponse).toList();
        GetAllResponse<CustomerViewProductListResponse> response = GetAllResponse.<CustomerViewProductListResponse>builder().data(data).pageNumber(page.getNumber()).pageSize(page.getSize()).totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).lastPage(page.isLast()).build();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<GetAllResponse<CustomerViewProductListResponse>> viewAllProductsCustomer(String query, String categoryId, Pageable pageable) {
        log.info("Starting product search for customer. CategoryId: [{}], Query: [{}]", categoryId, query);
        Specification<Product> spec = buildCustomerProductSpecification(categoryId, query);
        Page<Product> page = productRepo.findAll(spec, pageable);
        log.info("Query executed. Found {} total products matching criteria.", page.getTotalElements());
        return buildPaginatedResponseProductCustomer(page);
    }

    @Override
    public ResponseEntity<GetAllResponse<CustomerViewProductListResponse>> viewSimilarProducts(String productId, Pageable pageable) {
        log.debug("Customer viewing similar products for product ID: {}", productId);
        Product originalProduct = getProductForCustomerViewById(UUID.fromString(productId));

        Page<Product> page = productRepo.findSimilarProducts(originalProduct.getCategory().getId(), originalProduct.getId(), pageable);
        log.debug("Found {} similar products.", page.getTotalElements());
        return buildPaginatedResponseProductCustomer(page);
    }

    @Override
    public ResponseEntity<AdminViewProductResponse> viewProductAdmin(String productId) {
        log.debug("Admin viewing product with ID: {}", productId);
        Product product = productRepo.findById(UUID.fromString(productId)).orElseThrow(() -> new ProductNotFoundException(productId));
        AdminViewProductResponse response = mapProductToAdminViewResponse(product);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<GetAllResponse<AdminViewProductResponse>> viewAllProducts(String query, String categoryId, String sellerId, Boolean isActive, Pageable pageable) {
        log.debug("Admin searching all products with query: [{}], categoryId: [{}], sellerId: [{}], isActive: [{}]", query, categoryId, sellerId, isActive);
        Specification<Product> spec = buildAdminProductSpecification(query, categoryId, sellerId, isActive);

        Page<Product> page = productRepo.findAll(spec, pageable);
        log.debug("Admin query executed. Found {} total products.", page.getTotalElements());
        return buildPaginatedResponseProductAdmin(page);
    }


    @Override
    @Transactional
    public ResponseEntity<String> activateProduct(String productId) {
        log.debug("Admin attempting to activate product with ID: {}", productId);
        Product product = productRepo.findById(UUID.fromString(productId)).orElseThrow(() -> new ProductNotFoundException(productId));
        if (product.isActive()) {
            return ResponseEntity.ok(MessageUtil.getMessagePlaceHolder("product.already.active", productId));
        }
        product.setActive(true);
        productRepo.save(product);
        asyncEmailProcessor.activateProductEmail(product);
        log.info("Successfully activated product with ID: {}", productId);
        return ResponseEntity.ok(MessageUtil.getMessagePlaceHolder("product.activated.success", productId));
    }

    @Override
    @Transactional
    public ResponseEntity<String> deactivateProduct(String productId) {
        log.debug("Admin attempting to deactivate product with ID: {}", productId);
        Product product = productRepo.findById(UUID.fromString(productId)).orElseThrow(() -> new ProductNotFoundException(productId));

        if (!product.isActive()) {
            log.warn("Product {} is already inactive.", productId);
            return ResponseEntity.ok(MessageUtil.getMessagePlaceHolder("product.already.inactive", productId));
        }
        product.setActive(false);
        productRepo.save(product);
        asyncEmailProcessor.deActiveProductEmail(product);
        log.info("Successfully deactivated product with ID: {}", productId);
        return ResponseEntity.ok(MessageUtil.getMessagePlaceHolder("product.deactivated.success", productId));
    }

    private void validateVariationMetadata(Category category, Map<String, String> metadata) {
        List<CategoryMetadataFieldValues> definedFields = categoryMetadataFieldValuesRepo.findByCategory_Id(category.getId());

        if (definedFields.isEmpty() && !metadata.isEmpty()) {
            throw new ValidationException("productvariation.nometadata");
        }

        Map<String, Set<String>> possibleValuesMap = definedFields.stream().collect(Collectors.toMap(field -> field.getCategoryMetaDataField().getName(), field -> new HashSet<>(Arrays.asList(field.getValues().split(Consts.FIELDVALUESREGEX)))));

        if (!metadata.keySet().equals(possibleValuesMap.keySet())) {
            throw new ValidationException("productvariation.metadata.field.missing", String.valueOf(possibleValuesMap.keySet()));
        }

        for (Map.Entry<String, String> entry : metadata.entrySet()) {
            String fieldName = entry.getKey();
            String value = entry.getValue();
            Set<String> possibleValues = possibleValuesMap.get(fieldName);
            if (possibleValues == null || !possibleValues.contains(value)) {
                throw new ValidationException("productvariation.metadata.values.mismatch", value, fieldName, possibleValues == null ? "Not defined yet" : possibleValues.toString());
            }
        }
    }

    private ViewVariationResponse mapVariationToViewResponse(ProductVariation variation) {
        Product product = variation.getProduct();
        ViewProductResponse parentProductDetails = mapProductToViewResponse(product);
        Map<String, String> metadata;
        try {
            //  Java mein due to type erasure generic type information (like Map<String, Object> or List<MyClass>)
            //  runtime pe available nhi hoti isliye TypeRefernce
            metadata = objectMapper.readValue(variation.getMetadata(), new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("Failed to deserialize metadata for variation ID {}", variation.getId(), e);
            metadata = Collections.emptyMap();
        }
        return new ViewVariationResponse(variation.getId(), variation.getQuantityAvailable(), variation.getPrice(), variation.isActive(), fileService.getVariationImageUrl(variation), fileService.getSecondaryVariationImageUrls(variation), metadata, parentProductDetails);
    }

    private void setterProductUpdate(UpdateProductRequest updateProductRequest, Product product) {
        if (product != null) {
            if (updateProductRequest.name() != null) {
                product.setName(updateProductRequest.name().trim());
            }
            if (updateProductRequest.description() != null) {
                product.setDescription(updateProductRequest.description());
            }
            if (updateProductRequest.isCancellable() != null) {
                product.setActive(updateProductRequest.isCancellable());
            }
            if (updateProductRequest.isReturnable() != null) {
                product.setActive(updateProductRequest.isReturnable());
            }
        }
    }

    private void validatePartialVariationMetadata(Category category, Map<String, String> partialMetadata) {
        List<CategoryMetadataFieldValues> definedFields = categoryMetadataFieldValuesRepo.findByCategory_Id(category.getId());
        Map<String, Set<String>> possibleValuesMap = definedFields.stream().collect(Collectors.toMap(field -> field.getCategoryMetaDataField().getName(), field -> new HashSet<>(Arrays.asList(field.getValues().split(Consts.FIELDVALUESREGEX)))));
        for (Map.Entry<String, String> entry : partialMetadata.entrySet()) {
            String fieldName = entry.getKey();
            String valueToUpdate = entry.getValue();
            if (!possibleValuesMap.containsKey(fieldName)) {
                throw new ValidationException("productvariation.metadata.field.invalid", fieldName, String.valueOf(possibleValuesMap.keySet()));
            }
            Set<String> possibleValues = possibleValuesMap.get(fieldName);
            if (!possibleValues.contains(valueToUpdate)) {
                throw new ValidationException("productvariation.metadata.values.mismatch", valueToUpdate, fieldName, possibleValues.toString());
            }
        }
    }

    private CustomerViewProductResponse mapProductToCustomerViewResponse(Product product) {
        List<VariationDetail> variationDetails = product.getVariations().stream().filter(ProductVariation::isActive).map(this::mapVariationToDetail).toList();

        Map<String, Object> categoryInfo = Map.of("id", product.getCategory().getId(), "name", product.getCategory().getName());
        Map<String, Object> sellerInfo = Map.of("id", product.getSeller().getId(), "name", product.getSeller().getFirstName());

        return new CustomerViewProductResponse(product.getId(), product.getName(), product.getDescription(), product.getBrand(), product.isCancellable(), product.isReturnable(), categoryInfo, sellerInfo, variationDetails);
    }

    private VariationDetail mapVariationToDetail(ProductVariation variation) {
        Map<String, String> metadata;
        try {
            metadata = objectMapper.readValue(variation.getMetadata(), new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("Failed to deserialize metadata for variation ID {}", variation.getId(), e);
            metadata = Collections.emptyMap();
        }
        return new VariationDetail(variation.getId(), variation.getQuantityAvailable(), variation.getPrice(), fileService.getVariationImageUrl(variation), fileService.getSecondaryVariationImageUrls(variation), metadata);
    }

    private CustomerViewProductListResponse mapProductToCustomerListResponse(Product product) {
        String imageUrl = product.getVariations().stream().filter(ProductVariation::isActive).findFirst().map(fileService::getVariationImageUrl).orElse(null);

        return new CustomerViewProductListResponse(product.getId(), product.getName(), product.getBrand(), imageUrl);
    }


    private AdminViewProductResponse mapProductToAdminViewResponse(Product product) {
        log.debug("Mapping Product ID {} to AdminViewProductResponse.", product.getId());
        List<VariationInfo> variationInfos = product.getVariations().stream().map(this::mapVariationToInfo).toList();

        Map<String, Object> categoryInfo = Map.of("id", product.getCategory().getId(), "name", product.getCategory().getName());
        Map<String, Object> sellerInfo = Map.of("id", product.getSeller().getId(), "email", product.getSeller().getEmail());

        return new AdminViewProductResponse(product.getId(), product.getName(), product.getDescription(), product.getBrand(), product.isActive(), product.isCancellable(), product.isReturnable(), categoryInfo, sellerInfo, variationInfos);
    }

    private VariationInfo mapVariationToInfo(ProductVariation variation) {
        log.debug("Mapping Variation ID {} to VariationInfo.", variation.getId());
        Map<String, String> metadata;
        try {
            metadata = objectMapper.readValue(variation.getMetadata(), new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("Failed to deserialize metadata for variation ID {}", variation.getId(), e);
            metadata = Collections.emptyMap();
        }
        return new VariationInfo(variation.getId(), variation.getQuantityAvailable(), variation.getPrice(), variation.isActive(), fileService.getVariationImageUrl(variation), metadata);
    }

    private Seller getAuthenticatedSeller() {
        String sellerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("Fetching authenticated seller: {}", sellerEmail);
        return (Seller) userRepo.findByEmailIgnoreCase(sellerEmail).orElseThrow(UserNotFoundException::new);
    }

    private Product getProductForCustomerViewById(UUID productId) {
        log.debug("Fetching and validating product for customer view: {}", productId);
        Product product = productRepo.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId.toString()));
        if (!product.isActive() || product.getVariations().isEmpty()) {
            log.warn("Product {} is not active or has no variations. Hiding from customer.", productId);
            throw new ProductNotFoundException(productId.toString());
        }
        return product;
    }

    private Category getAndValidateCategoryIsLeaf(String categoryId) {
        log.debug("Fetching and validating category ID: {}", categoryId);
        Category category = categoryRepo.findById(UUID.fromString(categoryId.trim())).orElseThrow(() -> new CategoryNotFoundException(categoryId));
        if (!category.isLeaf()) {
            log.warn("Category {} is not a leaf node.", categoryId);
            throw new CategoryIsNotLeafException(categoryId);
        }
        return category;
    }

    private Product getProductAndVerifyOwnership(String productId) {
        log.debug("Fetching and verifying ownership for product ID: {}", productId);
        final Seller seller = getAuthenticatedSeller();
        Product product = productRepo.findById(UUID.fromString(productId)).orElseThrow(() -> new ProductNotFoundException(productId));
        if (!product.getSeller().getId().equals(seller.getId())) {
            log.warn("Access denied for seller {} to product {}", seller.getEmail(), productId);
            throw new AccessDeniedException();
        }
        return product;
    }

    private void validateImages(MultipartFile primaryImage, List<MultipartFile> secondaryImages) {
        log.debug("Validating images.");
        fileService.checkValid(primaryImage);
        if (secondaryImages != null && !secondaryImages.isEmpty()) {
            for (MultipartFile image : secondaryImages) {
                if (image != null && !image.isEmpty()) {
                    fileService.checkValid(image);
                }
            }
        }
        log.debug("Image validation successful.");
    }

    private ProductVariation createAndSaveInitialVariation(Product product, ProductVariationRequest request) {
        log.debug("Creating initial variation entity for product ID: {}", product.getId());
        ProductVariation variation = new ProductVariation();
        variation.setProduct(product);
        variation.setActive(true);
        variation.setQuantityAvailable(request.quantityAvailable());
        variation.setPrice(request.price());
        try {
            variation.setMetadata(objectMapper.writeValueAsString(request.metadata()));
        } catch (JsonProcessingException e) {
            throw new JsonSerializingException();
        }
        return productVariationRepo.save(variation);
    }

    private ResponseEntity<GetAllResponse<ViewProductResponse>> buildPaginatedResponseForProduct(Page<Product> page) {
        List<ViewProductResponse> data = page.getContent().stream().map(this::mapProductToViewResponse).toList();
        GetAllResponse<ViewProductResponse> response = GetAllResponse.<ViewProductResponse>builder().data(data).pageNumber(page.getNumber()).pageSize(page.getSize()).totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).lastPage(page.isLast()).build();
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<GetAllResponse<ViewVariationResponse>> buildPaginatedResponseProductVariations(Page<ProductVariation> page) {
        List<ViewVariationResponse> data = page.getContent().stream().map(this::mapVariationToViewResponse).toList();
        GetAllResponse<ViewVariationResponse> response = GetAllResponse.<ViewVariationResponse>builder().data(data).pageNumber(page.getNumber()).pageSize(page.getSize()).totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).lastPage(page.isLast()).build();
        return ResponseEntity.ok(response);
    }

    private ProductVariation getVariationAndVerifyOwnership(String variationId) {
        log.debug("Fetching and verifying ownership for variation ID: {}", variationId);
        ProductVariation variation = productVariationRepo.findById(UUID.fromString(variationId)).orElseThrow(() -> new ProductVariationNotFoundException(variationId));
        getProductAndVerifyOwnership(variation.getProduct().getId().toString()); // Reuses product ownership check
        return variation;
    }

    private void updateVariationFields(ProductVariation variation, UpdateProductVariationRequest request) {
        log.debug("Applying field updates to variation ID: {}", variation.getId());
        if (request.quantityAvailable() != null) {
            variation.setQuantityAvailable(request.quantityAvailable());
        }
        if (request.price() != null) {
            variation.setPrice(request.price());
        }
        if (request.isActive() != null) {
            variation.setActive(request.isActive());
        }
        if (request.metadata() != null && !request.metadata().isEmpty()) {
            validatePartialVariationMetadata(variation.getProduct().getCategory(), request.metadata());
            try {
                Map<String, String> existingMetadata = objectMapper.readValue(variation.getMetadata(), new TypeReference<>() {
                });
                existingMetadata.putAll(request.metadata());
                variation.setMetadata(objectMapper.writeValueAsString(existingMetadata));
            } catch (JsonProcessingException e) {
                throw new JsonSerializingException();
            }
        }
    }

    private void updateVariationImages(ProductVariation variation, MultipartFile primaryImage, List<MultipartFile> secondaryImages) throws IOException {
        log.debug("Applying image updates to variation ID: {}", variation.getId());
        if (primaryImage != null && !primaryImage.isEmpty()) {
            log.debug("Updating primary image.");
            fileService.checkValid(primaryImage);
            fileService.deletePrimaryVariationImage(variation);
            String newPrimaryImageName = fileService.uploadProductVariationImage(primaryImage, variation.getProduct().getId().toString(), variation);
            variation.setPrimaryImageName(newPrimaryImageName);
        }
        if (secondaryImages != null && !secondaryImages.isEmpty()) {
            log.debug("Updating secondary images.");
            validateImages(null, secondaryImages);
            fileService.deleteSecondaryVariationImages(variation);
            fileService.uploadSecondaryVariationImages(secondaryImages, variation.getProduct().getId().toString(), variation);
        }
    }

    private Specification<Product> buildCustomerProductSpecification(String categoryId, String query) {
        Specification<Product> spec = isActiveProduct(true).and(hasVariations());

        if (query != null && !query.isBlank()) {
            spec = spec.and(nameOrBrandContains(query));
            log.debug("Added filter: Name or Brand contains '{}'", query);
        }

        if (categoryId != null && !categoryId.isBlank()) {
            Category parentCategory = categoryRepo.findById(UUID.fromString(categoryId)).orElse(null);
            if (parentCategory != null) {
                log.debug("Found parent category: {}", parentCategory.getName());
                if (parentCategory.isLeaf()) {
                    spec = spec.and(inCategory(parentCategory.getId()));
                    log.debug("Filtering by single leaf category ID: {}", parentCategory.getId());
                } else {
                    List<Category> leafDescendants = new ArrayList<>();
                    categoryService.findAllLeafDescendants(parentCategory, leafDescendants);
                    if (!leafDescendants.isEmpty()) {
                        List<UUID> leafIds = leafDescendants.stream().map(Category::getId).toList();
                        log.debug("Found {} leaf descendant IDs: {}", leafIds.size(), leafIds);
                        spec = spec.and(inCategories(leafIds));
                    } else {
                        log.warn("Parent category '{}' has no leaf descendants. Returning empty spec.", parentCategory.getName());
                        spec = spec.and((root, q, cb) -> cb.disjunction());
                    }
                }
            } else {
                log.warn("Invalid categoryId provided: {}. Returning empty spec.", categoryId);
                spec = spec.and((root, q, cb) -> cb.disjunction());
            }
        } else {
            log.debug("No categoryId provided. Searching all categories.");
        }
        return spec;
    }

    private Specification<Product> buildAdminProductSpecification(String query, String categoryId, String sellerId, Boolean isActive) {
        Specification<Product> spec = Specification.unrestricted();

        if (query != null && !query.isBlank()) {
            spec = spec.and(nameOrBrandContains(query));
            log.debug("Admin filter added: Name or Brand contains '{}'", query);
        }
        if (categoryId != null && !categoryId.isBlank()) {
            spec = spec.and(inCategory(UUID.fromString(categoryId)));
            log.debug("Admin filter added: Category ID is '{}'", categoryId);
        }
        if (sellerId != null && !sellerId.isBlank()) {
            spec = spec.and(bySellerId(UUID.fromString(sellerId)));
            log.debug("Admin filter added: Seller ID is '{}'", sellerId);
        }
        if (isActive != null) {
            spec = spec.and(isActiveProduct(isActive));
            log.debug("Admin filter added: isActive is '{}'", isActive);
        }
        return spec;
    }

    private ResponseEntity<GetAllResponse<AdminViewProductResponse>> buildPaginatedResponseProductAdmin(Page<Product> page) {
        List<AdminViewProductResponse> data = page.getContent().stream().map(this::mapProductToAdminViewResponse).toList();
        GetAllResponse<AdminViewProductResponse> response = GetAllResponse.<AdminViewProductResponse>builder().data(data).pageNumber(page.getNumber()).pageSize(page.getSize()).totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).lastPage(page.isLast()).build();

        return ResponseEntity.ok(response);
    }

    private ViewProductResponse mapProductToViewResponse(Product product) {
        Map<String, Object> categoryInfo = Map.of(product.getCategory().getName(), product.getCategory().getId());
        return new ViewProductResponse(product.getId(), product.getName(), product.getDescription(), product.getBrand(), product.isCancellable(), product.isReturnable(), product.isActive(), categoryInfo);
    }
}

