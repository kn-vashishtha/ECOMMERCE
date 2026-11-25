package org.bootcamp.ecommerce.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bootcamp.ecommerce.dto.auth.ForgotPasswordRequest;
import org.bootcamp.ecommerce.dto.auth.ForgotPasswordTokenRequest;
import org.bootcamp.ecommerce.dto.auth.RefreshTokenRequest;
import org.bootcamp.ecommerce.dto.user.LoginRequest;
import org.bootcamp.ecommerce.dto.user.LoginResponse;
import org.bootcamp.ecommerce.security.services.AuthService;
import org.bootcamp.ecommerce.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PutMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@Valid @RequestParam("token") String token) {
        log.info("Received request to verify email with token.");
        return userService.verifyUserByEmailToken(token);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Received login attempt for user: {}", loginRequest.email());
        return authService.loginIn(loginRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        log.info("Received request to logout.");
        return authService.logOut();
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("Received request to refresh token.");
        return authService.refreshToken(refreshTokenRequest);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordTokenRequest forgotPasswordTokenRequest) {
        log.info("Received forgot password request for user: {}", forgotPasswordTokenRequest.email());
        return userService.forgotPassword(forgotPasswordTokenRequest);
    }

    @PutMapping("/forgot-password-using-token")
    public ResponseEntity<String> forgotPasswordUsingToken(@RequestParam("token") String token,
                                                           @Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        log.info("Received request to reset password using token.");
        return userService.forgotPasswordUsingToken(token, forgotPasswordRequest);
    }
}
