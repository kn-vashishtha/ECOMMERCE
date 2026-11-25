package org.bootcamp.ecommerce.security.services.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bootcamp.ecommerce.constants.Consts;
import org.bootcamp.ecommerce.security.services.JWTService;
import org.bootcamp.ecommerce.utils.SimpleEncryptorUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JWTServiceImpl implements JWTService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    private final SimpleEncryptorUtil simpleEncryptorUtil;

    @Override
    public String generateToken(UserDetails userDetails, String refreshToken) {
        log.trace("Generating JWT token for user: {}", userDetails.getUsername());
        String encryptedUsername = simpleEncryptorUtil.encrypt(userDetails.getUsername());
        log.trace("Encrypted username: {}", encryptedUsername);
        String token = Jwts
                .builder()
                .setSubject(encryptedUsername)
                .claim(Consts.JWT_KEY, refreshToken)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
        log.trace("Generated JWT token: {}", token);
        return token;
    }

    @Override
    public String generateRefreshToken(HashMap<String, Object> extraClaims, UserDetails userDetails, String refreshTokenId) {
        log.trace("Generating refresh token for user: {}", userDetails.getUsername());
        log.trace("Extra claims: {}", extraClaims);
        String encryptedUsername = simpleEncryptorUtil.encrypt(userDetails.getUsername());
        log.trace("Encrypted username for refresh token: {}", encryptedUsername);
        String refreshToken = Jwts
                .builder()
                .setClaims(extraClaims)
                .claim(Consts.JWT_KEY, refreshTokenId)
                .setSubject(encryptedUsername)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24 * 7))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
        log.trace("Generated refresh token: {}", refreshToken);
        return refreshToken;
    }

    private Key getSignInKey() {
        log.trace("Decoding secret key for JWT signing");
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        log.trace("Generated signing key");
        return key;
    }

    private Claims extractAllClaims(String token) {
        log.trace("Extracting all claims from token");
        if (token == null || token.isBlank()) {
            log.trace("Token is missing or blank");
            throw new BadCredentialsException("Missing or empty JWT token");
        }
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        log.trace("Extracted claims: {}", claims);
        return claims;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        log.trace("Extracting claim from token");
        final Claims claims = extractAllClaims(token);
        T result = claimsResolver.apply(claims);
        log.trace("Extracted claim value: {}", result);
        return result;
    }

    @Override
    public String extractUsername(String token) {
        log.trace("Extracting username from token");
        String extractClaim = extractClaim(token, Claims::getSubject);
        String username = simpleEncryptorUtil.decrypt(extractClaim);
        log.trace("Decrypted username: {}", username);
        return username;
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        log.trace("Validating token for user: {}", userDetails.getUsername());
        final String username = extractUsername(token);
        boolean valid = (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        log.trace("Token valid: {}", valid);
        return valid;
    }

    @Override
    public boolean isTokenExpired(String token) {
        log.trace("Checking if token is expired");
        boolean expired = extractExpiration(token).before(new Date());
        log.trace("Token expired: {}", expired);
        return expired;
    }

    private Date extractExpiration(String token) {
        log.trace("Extracting expiration date from token");
        Date expiration = extractClaim(token, Claims::getExpiration);
        log.trace("Token expiration date: {}", expiration);
        return expiration;
    }

    public String extractRefreshTokenId(String token) {
        log.trace("Extracting refresh token ID from token");
        String refreshTokenId = extractClaim(token, claims -> claims.get(Consts.JWT_KEY, String.class));
        log.trace("Extracted refresh token ID: {}", refreshTokenId);
        return refreshTokenId;
    }


}
