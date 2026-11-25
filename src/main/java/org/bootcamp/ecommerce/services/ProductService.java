package org.bootcamp.ecommerce.services;

import org.bootcamp.ecommerce.dto.auth.GetAllResponse;
import org.bootcamp.ecommerce.dto.product.*;
import org.bootcamp.ecommerce.dto.product.admin.AdminViewProductResponse;
import org.bootcamp.ecommerce.dto.product.customer.CustomerViewProductListResponse;
import org.bootcamp.ecommerce.dto.product.customer.CustomerViewProductResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    ResponseEntity<String> addProduct(AddProductRequest addProductRequest);

    ResponseEntity<ViewProductResponse> viewProduct(String productId);

    ResponseEntity<GetAllResponse<ViewProductResponse>> viewAllProducts(String query, String categoryId,
                                                                        Pageable pageable);

    ResponseEntity<String> deleteProduct(String productId);

    ResponseEntity<String> activateProduct(String productId);

    ResponseEntity<String> deactivateProduct(String productId);

    ResponseEntity<String> addProductVariation(ProductVariationRequest productVariationRequest, MultipartFile primaryImage, List<MultipartFile> secondaryImages) throws IOException;

    ResponseEntity<ViewVariationResponse> viewProductVariation(String id);

    ResponseEntity<GetAllResponse<ViewVariationResponse>> viewAllProductVariations(String query, String productId,
                                                                                   Boolean isActive,
                                                                                   Pageable pageable);

    ResponseEntity<String> updateProduct(UpdateProductRequest updateProductRequest);

    ResponseEntity<String> updateProductVariation(UpdateProductVariationRequest request, MultipartFile primaryImage,
                                                  List<MultipartFile> secondaryImages) throws IOException;

    ResponseEntity<CustomerViewProductResponse> viewProductCustomer(String productId);

    ResponseEntity<GetAllResponse<CustomerViewProductListResponse>> viewAllProductsCustomer(String query, String categoryId,
                                                                                            Pageable pageable);

    ResponseEntity<GetAllResponse<CustomerViewProductListResponse>> viewSimilarProducts(String productId,
                                                                                        Pageable pageable);

    ResponseEntity<AdminViewProductResponse> viewProductAdmin(String productId);

    ResponseEntity<GetAllResponse<AdminViewProductResponse>> viewAllProducts(
            String query, String categoryId, String sellerId, Boolean isActive, Pageable pageable);


}
