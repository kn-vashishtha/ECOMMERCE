package org.bootcamp.ecommerce.entity.compositekeys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class CartId {

    @Column(name = "customer_user_id")
    private UUID customerId;

    @Column(name = "product_variation_id")
    private UUID productVariationId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CartId cartId = (CartId) o;
        return Objects.equals(customerId, cartId.customerId) && Objects.equals(productVariationId, cartId.productVariationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, productVariationId);
    }
}
