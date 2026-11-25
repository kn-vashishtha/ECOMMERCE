package org.bootcamp.ecommerce.security.services;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;

public interface JWTService {
    String extractUsername(String token);

    String generateToken(UserDetails userDetails, String refreshToken);
    boolean isTokenValid(String token, UserDetails userDetails);

    String generateRefreshToken(HashMap<String, Object> extraClaims, UserDetails userDetails, String refreshTokenId);
    boolean isTokenExpired(String token);

    String extractRefreshTokenId(String token);
}
