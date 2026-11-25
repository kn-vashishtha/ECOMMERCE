package org.bootcamp.ecommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bootcamp.ecommerce.enums.Label;
import org.bootcamp.ecommerce.enums.PaymentMode;

import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "orders")
public class Order extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "customer_user_id")
    private Customer customer;

    @Column(name = "amount_paid")
    private double amountPaid;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "payment_mode")
    private PaymentMode paymentMode;

    @Column(name = "customer_address_city", nullable = false)
    private String customerAddressCity;

    @Column(name = "customer_address_state", nullable = false)
    private String customerAddressState;

    @Column(name = "customer_address_country", nullable = false)
    private String customerAddressCountry;

    @Column(name = "customer_address_line", nullable = false)
    private String customerAddressLine;

    @Column(name = "customer_address_zip_code", nullable = false)
    private String customerAddressZipCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_address_label", nullable = false)
    private Label customerAddressLabel;

    @OneToMany
    @JoinColumn(name = "order_id")
    private List<OrderProduct> orderProducts;
}
