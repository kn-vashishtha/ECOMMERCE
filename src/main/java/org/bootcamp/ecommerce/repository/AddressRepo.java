package org.bootcamp.ecommerce.repository;

import org.bootcamp.ecommerce.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AddressRepo extends JpaRepository<Address, UUID> {
}
