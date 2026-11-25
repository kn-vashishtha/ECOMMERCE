package org.bootcamp.ecommerce.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bootcamp.ecommerce.enums.OrderStatusEnum;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_status")
public class OrderStatus extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "order_product_id", nullable = false)
    private OrderProduct orderProduct;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", nullable = false)
    private OrderStatusEnum fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false)
    private OrderStatusEnum toStatus;

    @Column(name = "transition_notes_comments", nullable = false)
    private String transitionNotesComments;

    @Column(name = "transition_date", nullable = false)
    private String transitionDate;

}
