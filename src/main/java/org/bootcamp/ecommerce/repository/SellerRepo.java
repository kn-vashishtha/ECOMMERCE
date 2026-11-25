package org.bootcamp.ecommerce.repository;

import org.bootcamp.ecommerce.entity.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SellerRepo extends JpaRepository<Seller, UUID> {
    boolean existsByCompanyNameIgnoreCase(String companyName);

    boolean existsByGstIgnoreCase(String gstNumber);

    Page<Seller> findByEmailContainingIgnoreCase(String email, Pageable pageable);

}
