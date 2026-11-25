package org.bootcamp.ecommerce.repository;

import org.bootcamp.ecommerce.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface BlacklistedTokenRepo extends JpaRepository<BlacklistedToken, UUID> {
    long deleteByBlacklistedAtBefore(LocalDateTime cutoff);

}
