package org.bootcamp.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.bootcamp.ecommerce.enums.Label;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@SQLRestriction("is_deleted = false")
@SQLDelete(sql = "UPDATE addresses SET is_deleted = true WHERE address_id = ?")
public class Address extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "address_id")
    private UUID id;
    @Column(name = "city")
    private String city;
    @Column(name = "state")
    private String state;
    @Column(name = "country")
    private String country;
    @Column(name = "address_line")
    @JsonProperty("addressline")
    private String addressLine;
    @Column(name = "zip_code")
    @JsonProperty("zipcode")
    private String zipCode;
    @Enumerated(value = EnumType.STRING)
    private Label label;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}
