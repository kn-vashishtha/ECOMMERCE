package org.bootcamp.ecommerce.security.services.impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bootcamp.ecommerce.dto.auth.RefreshTokenRequest;
import org.bootcamp.ecommerce.dto.user.LoginRequest;
import org.bootcamp.ecommerce.dto.user.LoginResponse;
import org.bootcamp.ecommerce.entity.BlacklistedToken;
import org.bootcamp.ecommerce.entity.User;
import org.bootcamp.ecommerce.enums.RequestType;
import org.bootcamp.ecommerce.exception.AccountLockedException;
import org.bootcamp.ecommerce.exception.TokenBlacklistedException;
import org.bootcamp.ecommerce.exception.TokenExpiredException;
import org.bootcamp.ecommerce.exception.UserNotFoundException;
import org.bootcamp.ecommerce.processors.AsyncEmailProcessor;
import org.bootcamp.ecommerce.repository.BlacklistedTokenRepo;
import org.bootcamp.ecommerce.repository.UserRepo;
import org.bootcamp.ecommerce.security.UserPrinciple;
import org.bootcamp.ecommerce.security.services.AuthService;
import org.bootcamp.ecommerce.security.services.JWTService;
import org.bootcamp.ecommerce.utils.SimpleEncryptorUtil;
import org.bootcamp.ecommerce.utils.validators.EmailValidator;
import org.bootcamp.ecommerce.utils.validators.PasswordValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

import static org.bootcamp.ecommerce.utils.validators.UserPrincipleValidator.checkValidUserPrinciple;

@Service
@Data
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final BlacklistedTokenRepo blacklistedTokenRepo;
    private final SimpleEncryptorUtil simpleEncryptorUtil;
    private final AsyncEmailProcessor asyncEmailProcessor;

    public ResponseEntity<LoginResponse> loginIn(LoginRequest loginRequest) {
        log.info("Attempting login for email={}", loginRequest.email());
        EmailValidator.validateEmail(loginRequest.email().trim());
        PasswordValidator.isValidPassword(loginRequest.password());
        var user = userRepo.findByEmailIgnoreCase(loginRequest.email()).orElseThrow(UserNotFoundException::new);
        try {
            log.debug("User fetched for email={}", loginRequest.email());
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email().trim(), loginRequest.password()));
            if (!authenticate.isAuthenticated()) {
                throw new BadCredentialsException("Invalid credentials");
            }
            log.debug("Authentication successful for email={}", loginRequest.email());
            user.setInValidAttemptCount(0);
            UserPrinciple userPrinciple = (UserPrinciple) authenticate.getPrincipal();

            String refreshTokenId = UUID.randomUUID().toString();
            String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), userPrinciple, refreshTokenId);
            String accessToken = jwtService.generateToken(userPrinciple, refreshTokenId);
            log.info("Generated access and refresh tokens for email={}", loginRequest.email());
            userRepo.save(user);
            return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));
        } catch (BadCredentialsException e) {
            int count = user.getInValidAttemptCount() + 1;
            user.setInValidAttemptCount(count);
            if (count >= 3) {
                user.setLocked(true);
                userRepo.save(user);
                log.warn("Account locked due to multiple failed login attempts for email={}", loginRequest.email());
                asyncEmailProcessor.emailSender(user.getEmail(), null, RequestType.ACCOUNT_LOCKED);
                throw new AccountLockedException();
            }
            log.warn("Invalid credentials for email={}", loginRequest.email());
            userRepo.save(user);
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    public ResponseEntity<LoginResponse> refreshToken(RefreshTokenRequest refreshToken) {
        log.info("Attempting to refresh token");
        String providedRefresh = refreshToken.refreshToken();
        if (jwtService.isTokenExpired(providedRefresh)) {
            log.warn("Refresh token expired");
            throw new TokenExpiredException();
        }
        String providedRefreshTokenId = jwtService.extractRefreshTokenId(providedRefresh);
        UUID uuid = UUID.fromString(providedRefreshTokenId);
        if (blacklistedTokenRepo.existsById(uuid)) {
            log.warn("Refresh token is blacklisted");
            throw new TokenBlacklistedException();
        }
        BlacklistedToken blockToken = new BlacklistedToken(uuid, LocalDateTime.now());
        blacklistedTokenRepo.save(blockToken);
        String email = jwtService.extractUsername(providedRefresh);
        User user = userRepo.findByEmailIgnoreCase((email)).orElseThrow(UserNotFoundException::new);
        UserPrinciple userPrinciple = new UserPrinciple(user);
        checkValidUserPrinciple(userPrinciple);
        String newRefreshTokenId = UUID.randomUUID().toString();
        String newRefresh = jwtService.generateRefreshToken(new HashMap<>(), userPrinciple, newRefreshTokenId);
        String newAccessToken = jwtService.generateToken(userPrinciple, newRefreshTokenId);
        log.info("Issued new access token for username={}", email);
        return ResponseEntity.ok(new LoginResponse(newAccessToken, newRefresh));
    }

    @Override
    public ResponseEntity<String> logOut() {
        String accessToken = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        if (jwtService.isTokenExpired(accessToken)) {
            throw new TokenExpiredException();
        }
        UUID uuid = UUID.fromString(jwtService.extractRefreshTokenId(accessToken));
        if (!blacklistedTokenRepo.existsById(uuid)) {
            BlacklistedToken blacklistedToken = new BlacklistedToken(uuid, LocalDateTime.now());
            blacklistedTokenRepo.save(blacklistedToken);
        }
        log.info("Logout successful");
        return ResponseEntity.ok("Logout successful");
    }
}
