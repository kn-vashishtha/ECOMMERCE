package org.bootcamp.ecommerce.utils;

import org.bootcamp.ecommerce.entity.Product;
import org.bootcamp.ecommerce.entity.ProductVariation;
import org.bootcamp.ecommerce.entity.Seller;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public class SpecificationUtil {
    private SpecificationUtil() {
    }

    public static Specification<Product> nameOrBrandContains(String query) {
        String searchTerm = "%" + query.toLowerCase() + "%";
        return (root, q, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), searchTerm),
                cb.like(cb.lower(root.get("brand")), searchTerm)
        );
    }

    public static Specification<Product> inCategory(UUID categoryId) {
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> bySeller(Seller seller) {
        return (root, query, cb) -> cb.equal(root.get("seller"), seller);
    }

    public static Specification<ProductVariation> forProduct(Product product) {
        return (root, query, cb) -> cb.equal(root.get("product"), product);
    }

    public static Specification<ProductVariation> metadataContains(String text) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("metadata")), "%" + text.toLowerCase() + "%");
    }

    public static Specification<ProductVariation> isActiveProductVariation(boolean status) {
        return (root, query, cb) -> cb.equal(root.get("isActive"), status);
    }

    public static Specification<Product> hasVariations() {
        return (root, query, cb) -> cb.isNotEmpty(root.get("variations"));
    }

    public static Specification<Product> inCategories(List<UUID> categoryIds) {
        return (root, query, cb) -> root.get("category").get("id").in(categoryIds);
    }

    public static Specification<Product> isActiveProduct(boolean status) {
        return (root, query, cb) -> cb.equal(root.get("isActive"), status);
    }


    public static Specification<Product> bySellerId(UUID sellerId) {
        return (root, query, cb) -> cb.equal(root.get("seller").get("id"), sellerId);
    }

}
