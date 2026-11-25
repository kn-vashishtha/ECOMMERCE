package org.bootcamp.ecommerce.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bootcamp.ecommerce.repository.BlacklistedTokenRepo;
import org.bootcamp.ecommerce.services.BlacklistService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlacklistServiceImpl implements BlacklistService {
    private final BlacklistedTokenRepo blacklistedTokenRepo;
    @Override
    public boolean isBlacklisted(String token) {
        log.debug("Checking if token is blacklisted: {}", token);
        UUID tokenId = UUID.fromString(token);
        boolean result = blacklistedTokenRepo.existsById(tokenId);
        log.debug("Token {} blacklisted: {}", token, result);
        return result;
    }
}
