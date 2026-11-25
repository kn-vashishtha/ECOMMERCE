package org.bootcamp.ecommerce.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bootcamp.ecommerce.dto.address.AddressRequestCustomer;
import org.bootcamp.ecommerce.dto.address.AddressResponseDTO;
import org.bootcamp.ecommerce.dto.customer.UpdateCustomerRequest;
import org.bootcamp.ecommerce.dto.customer.ViewCustomerProfileResponse;
import org.bootcamp.ecommerce.dto.seller.UpdateAddress;
import org.bootcamp.ecommerce.dto.user.UpdatePassword;
import org.bootcamp.ecommerce.services.CustomerService;
import org.bootcamp.ecommerce.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer")
public class CustomerController {
    private final CustomerService customerService;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ViewCustomerProfileResponse> viewMyProfile(Authentication authentication) {
        log.info("viewMyProfile called for customer: {}", authentication.getName());
        return customerService.viewMyProfile(authentication.getName());
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressResponseDTO>> viewAddresses(Authentication authentication) {
        log.info("viewAddresses called for customer: {}", authentication.getName());
        return customerService.viewAddresses(authentication.getName());
    }

    @PatchMapping(value = "/update-profile", consumes = {"multipart/form-data"})
    public ResponseEntity<String> updateProfile(@Valid @RequestPart(value = "data", required = false) UpdateCustomerRequest updateCustomerRequest,
                                                @RequestPart(value = "image", required = false) MultipartFile image,
                                                Authentication authentication) {
        log.info("updateProfile called for customer: {}, updateCustomerRequest: {}, image: {}", authentication.getName(), updateCustomerRequest, image != null);
        return customerService.updateProfile(updateCustomerRequest, image, authentication.getName());
    }

    @PatchMapping("/update-password")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody UpdatePassword updatePassword,
                                                 Authentication authentication) {
        log.info("updatePassword called for customer: {}", authentication.getName());
        return userService.updatePassword(updatePassword, authentication.getName());
    }

    @PostMapping("/add-address")
    public ResponseEntity<String> addAddress(@Valid @RequestBody AddressRequestCustomer addressRequest,
                                             Authentication authentication) {
        log.info("addAddress called for customer: {}, addressRequest: {}", authentication.getName(), addressRequest);
        return customerService.addAddress(addressRequest, authentication.getName());
    }

    @DeleteMapping("/delete-address/{id}")
    public ResponseEntity<String> deleteAddress(@PathVariable("id") String id, Authentication authentication) {
        log.info("deleteAddress called for customer: {}, addressId: {}", authentication.getName(), id);
        return customerService.deleteAddress(id, authentication.getName());
    }

    @PatchMapping("/update-address")
    public ResponseEntity<String> updateAddress(@Valid @RequestBody UpdateAddress updateAddress,
                                                Authentication authentication) {
        log.info("updateAddress called for customer: {}, updateAddress: {}", authentication.getName(), updateAddress);
        return userService.updateAddress(updateAddress, authentication);
    }
}
