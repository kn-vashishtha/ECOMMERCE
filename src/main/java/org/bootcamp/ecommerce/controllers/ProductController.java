package org.bootcamp.ecommerce.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bootcamp.ecommerce.dto.auth.GetAllResponse;
import org.bootcamp.ecommerce.dto.product.*;
import org.bootcamp.ecommerce.dto.product.admin.AdminViewProductResponse;
import org.bootcamp.ecommerce.dto.product.customer.CustomerViewProductListResponse;
import org.bootcamp.ecommerce.dto.product.customer.CustomerViewProductResponse;
import org.bootcamp.ecommerce.services.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {
    private final ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<String> addProduct(@Valid @RequestBody AddProductRequest addProductRequest) {
        log.info("addProduct called with request: {}", addProductRequest);
        return productService.addProduct(addProductRequest);
    }

    @PostMapping(value = "/add-variation", consumes = {"multipart/form-data"})
    public ResponseEntity<String> addProductVariation(
            @Valid @RequestPart("variation") ProductVariationRequest productVariationRequest,
            @RequestPart(value = "primary_image") MultipartFile primaryImage,
            @RequestPart(required = false, value = "secondary_images") List<MultipartFile> secondaryImages
    ) throws IOException {
        log.info("addProductVariation called with variation: {}, primaryImage: {}, secondaryImages: {}", productVariationRequest, primaryImage != null, secondaryImages != null ? secondaryImages.size() : 0);
        return productService.addProductVariation(productVariationRequest, primaryImage, secondaryImages);
    }

    @GetMapping()
    public ResponseEntity<ViewProductResponse> viewProduct(@RequestParam(name = "id") String productId) {
        log.info("viewProduct called with productId: {}", productId);
        return productService.viewProduct(productId);
    }

    @GetMapping("/variations")
    public ResponseEntity<ViewVariationResponse> viewProductVariationById(@RequestParam String id) {
        log.info("viewProductVariationById called with id: {}", id);
        return productService.viewProductVariation(id);
    }

    @GetMapping("/all")
    public ResponseEntity<GetAllResponse<ViewProductResponse>> viewAllProducts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String categoryId,
            @PageableDefault Pageable pageable
    ) {
        log.info("viewAllProducts called with query: {}, categoryId: {}, pageable: {}", query, categoryId, pageable);
        return productService.viewAllProducts(query, categoryId, pageable);
    }

    @GetMapping("/variations/all")
    public ResponseEntity<GetAllResponse<ViewVariationResponse>> viewAllProductVariations(
            @RequestParam(required = false) String query,
            @RequestParam String productId,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault Pageable pageable
    ) {
        log.info("viewAllProductVariations called with query: {}, productId: {}, isActive: {}, pageable: {}", query, productId, isActive, pageable);
        return productService.viewAllProductVariations(query, productId, isActive, pageable);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteProduct(@RequestParam(name = "id") String productId) {
        log.info("deleteProduct called with productId: {}", productId);
        return productService.deleteProduct(productId);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateProduct(@Valid @RequestBody UpdateProductRequest updateProductRequest) {
        log.info("updateProduct called with request: {}", updateProductRequest);
        return productService.updateProduct(updateProductRequest);
    }

    @PutMapping(value = "/variations/update", consumes = {"multipart/form-data"})
    public ResponseEntity<String> updateProductVariation(
            @Valid @RequestPart("variation") UpdateProductVariationRequest updateProductVariationRequest,
            @RequestPart(value = "primary_image", required = false) MultipartFile primaryImage,
            @RequestPart(required = false, value = "secondary_images") List<MultipartFile> secondaryImages
    ) throws IOException {
        log.info("updateProductVariation called with variation: {}, primaryImage: {}, secondaryImages: {}", updateProductVariationRequest, primaryImage != null, secondaryImages != null ? secondaryImages.size() : 0);
        return productService.updateProductVariation(updateProductVariationRequest, primaryImage, secondaryImages);
    }

    @GetMapping("/customer")
    public ResponseEntity<CustomerViewProductResponse> getProductById(@RequestParam("id") String id) {
        log.info("getProductById called with id: {}", id);
        return productService.viewProductCustomer(id);
    }

    @GetMapping("/customer/all")
    public ResponseEntity<GetAllResponse<CustomerViewProductListResponse>> viewAllProductsCustomers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String categoryId,
            @PageableDefault Pageable pageable
    ) {
        log.info("viewAllProductsCustomers called with query: {}, categoryId: {}, pageable: {}", query, categoryId, pageable);
        return productService.viewAllProductsCustomer(query, categoryId, pageable);
    }

    @GetMapping("/customer/similar")
    public ResponseEntity<GetAllResponse<CustomerViewProductListResponse>> getSimilarProducts(
            @RequestParam("id") String productId,
            Pageable pageable) {
        log.info("getSimilarProducts called with productId: {}, pageable: {}", productId, pageable);
        return productService.viewSimilarProducts(productId, pageable);
    }

    @GetMapping("/admin")
    public ResponseEntity<AdminViewProductResponse> viewProductById(@RequestParam("id") String id) {
        log.info("viewProductById (admin) called with id: {}", id);
        return productService.viewProductAdmin(id);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<GetAllResponse<AdminViewProductResponse>> viewAllProducts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String sellerId,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault Pageable pageable) {
        log.info("viewAllProducts (admin) called with query: {}, categoryId: {}, sellerId: {}, isActive: {}, pageable: {}", query, categoryId, sellerId, isActive, pageable);
        return productService.viewAllProducts(query, categoryId, sellerId, isActive, pageable);
    }

    @PutMapping("/admin/activate")
    public ResponseEntity<String> activateProduct(@RequestParam(name = "id") String productId) {
        log.info("activateProduct called with productId: {}", productId);
        return productService.activateProduct(productId);
    }

    @PutMapping("/admin/deactivate")
    public ResponseEntity<String> deactivateProduct(@RequestParam(name = "id") String productId) {
        log.info("deactivateProduct called with productId: {}", productId);
        return productService.deactivateProduct(productId);
    }

}
