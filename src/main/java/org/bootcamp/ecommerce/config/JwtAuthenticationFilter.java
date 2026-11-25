package org.bootcamp.ecommerce.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bootcamp.ecommerce.exception.TokenBlacklistedException;
import org.bootcamp.ecommerce.security.UserPrinciple;
import org.bootcamp.ecommerce.security.services.JWTService;
import org.bootcamp.ecommerce.security.services.impl.SecurityUserDetailsService;
import org.bootcamp.ecommerce.services.BlacklistService;
import org.bootcamp.ecommerce.utils.validators.EmailValidator;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

import static org.bootcamp.ecommerce.utils.validators.UserPrincipleValidator.checkValidUserPrinciple;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final SecurityUserDetailsService securityUserDetailsService;
    private final BlacklistService blackListService;


    private static String extractJwtFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        log.debug("Processing {} {}", request.getMethod(), request.getRequestURI());
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.trace("No Bearer token found in Authorization header");
            return null;
        }
        return authHeader.substring(7);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String jwtFromRequest = extractJwtFromRequest(request);
        if (jwtFromRequest == null || jwtFromRequest.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            final String userEmail = jwtService.extractUsername(jwtFromRequest);
            log.debug("Extracted username from JWT: {}", userEmail);
            EmailValidator.validateEmail(userEmail);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (userEmail != null && authentication == null) {
                UserPrinciple userPrinciple = this.securityUserDetailsService.loadUserByUsername(userEmail);
                checkValidUserPrinciple(userPrinciple);
                String refreshTokenId = jwtService.extractRefreshTokenId(jwtFromRequest);
                if (blackListService.isBlacklisted(refreshTokenId)) {
                    throw new TokenBlacklistedException();
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userPrinciple,
                        jwtFromRequest,
                        userPrinciple.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("User {} authenticated with authorities: {}", userEmail, userPrinciple.getAuthorities());
                log.info("Authenticated user: {}", userEmail);
            } else {
                log.trace("Security context already has authentication for request");
            }
            filterChain.doFilter(request, response);
        } catch (BadCredentialsException badCredentialsException) {
            log.warn("Invalid JWT token for user {}", badCredentialsException.getMessage());
        } catch (Exception exception) {
            log.error("JWT filter error for path {}", request.getRequestURI(), exception);
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }

    }


}
