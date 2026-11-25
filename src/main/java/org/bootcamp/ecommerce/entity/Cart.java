package org.bootcamp.ecommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bootcamp.ecommerce.entity.compositekeys.CartId;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "carts")
public class Cart extends Auditable {

    @EmbeddedId
    private CartId cartId = new CartId();
    @MapsId("customerId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_user_id")
    private Customer customer;
    @MapsId(value = "productVariationId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variation_id")
    private ProductVariation productVariation;
    @Column(name = "quantity")
    private int quantity;
    @Column(name = "is_wishlist_item")
    private boolean isWishList;

}
