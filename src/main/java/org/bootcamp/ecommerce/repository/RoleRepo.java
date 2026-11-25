package org.bootcamp.ecommerce.repository;

import org.bootcamp.ecommerce.entity.Role;
import org.bootcamp.ecommerce.enums.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepo extends JpaRepository<Role, UUID> {
    Optional<Role> findByAuthority(Authority authority);

    boolean existsByAuthority(Authority authority);
}
