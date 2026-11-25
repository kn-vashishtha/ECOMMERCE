package org.bootcamp.ecommerce.repository;

import org.bootcamp.ecommerce.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepo extends JpaRepository<Customer, UUID> {
    Page<Customer> findByEmailContainingIgnoreCase(String email, Pageable pageable);
}
