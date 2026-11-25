package org.bootcamp.ecommerce.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bootcamp.ecommerce.dto.seller.UpdateAddress;
import org.bootcamp.ecommerce.dto.seller.UpdateSellerRequest;
import org.bootcamp.ecommerce.dto.seller.ViewSellerProfileResponse;
import org.bootcamp.ecommerce.dto.user.UpdatePassword;
import org.bootcamp.ecommerce.services.SellerService;
import org.bootcamp.ecommerce.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/seller")
public class SellerController {
    private final SellerService sellerService;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ViewSellerProfileResponse> viewMyProfile(Authentication authentication) {
        log.info("viewMyProfile called for seller: {}", authentication.getName());
        return sellerService.viewMyProfile(authentication.getName());
    }

    @PatchMapping(
            value = "/update-profile",
            consumes = {"multipart/form-data"}
    )
    public ResponseEntity<String> updateProfile(@Valid @RequestPart(value = "data", required = false) UpdateSellerRequest updateSellerRequest,
                                                @RequestPart(value = "image", required = false) MultipartFile image,
                                                Authentication authentication) {
        log.info("updateProfile called for seller: {}, updateSellerRequest: {}, image: {}", authentication.getName(), updateSellerRequest, image != null);
        return sellerService.updateProfile(updateSellerRequest, image, authentication.getName());
    }

    @PatchMapping("/update-password")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody UpdatePassword updatePassword,
                                                 Authentication authentication) {
        log.info("updatePassword called for seller: {}", authentication.getName());
        return userService.updatePassword(updatePassword, authentication.getName());
    }

    @PatchMapping("/update-address")
    public ResponseEntity<String> updateAddress(@Valid @RequestBody UpdateAddress updateAddress,
                                                Authentication authentication) {
        log.info("updateAddress called for seller: {}, updateAddress: {}", authentication.getName(), updateAddress);
        return userService.updateAddress(updateAddress, authentication);
    }

}
