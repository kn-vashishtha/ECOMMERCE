package org.bootcamp.ecommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.bootcamp.ecommerce.utils.convertors.CryptoAttributeConvertor;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "customers")
public class Customer extends User {

    @Convert(converter = CryptoAttributeConvertor.class)
    @Column(name = "contact")
    private String contact;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;

}
