package org.bootcamp.ecommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_variations")
@SQLDelete(sql = "UPDATE product_variations SET is_deleted = true WHERE product_variation_id = ?")
@SQLRestriction("is_deleted = false")
public class ProductVariation extends Auditable {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    @Column(name = "product_variation_id")
    private UUID id;

    @Column(name = "quantity_available")
    private int quantityAvailable;
    @Column(name = "price")
    private double price;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    @Column(name = "primary_image_name")
    private String primaryImageName;

    @Column(name = "is_active")
    private boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "is_deleted")
    Boolean isDeleted = false;

}
