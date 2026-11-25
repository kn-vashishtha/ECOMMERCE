package org.bootcamp.ecommerce.services;

public interface BlacklistService {
    boolean isBlacklisted(String token);
}
