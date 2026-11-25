package org.bootcamp.ecommerce.security.services;

import org.bootcamp.ecommerce.dto.auth.RefreshTokenRequest;
import org.bootcamp.ecommerce.dto.user.LoginRequest;
import org.bootcamp.ecommerce.dto.user.LoginResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<LoginResponse> loginIn(LoginRequest loginRequest);

    ResponseEntity<LoginResponse> refreshToken(RefreshTokenRequest refreshToken);

    ResponseEntity<String> logOut();
}
