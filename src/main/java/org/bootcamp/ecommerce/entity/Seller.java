package org.bootcamp.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder
@Table(name = "sellers")
public class Seller extends User {
    @Column(name = "gst", unique = true)
    private String gst;
    @Column(name = "company_contact")
    private String companyContact;
    @Column(name = "company_name", unique = true)
    private String companyName;
    @OneToMany(mappedBy = "seller")
    private List<Product> products;

}
