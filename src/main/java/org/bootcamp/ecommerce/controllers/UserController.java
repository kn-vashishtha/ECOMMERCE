package org.bootcamp.ecommerce.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bootcamp.ecommerce.dto.customer.CustomerResponseRegister;
import org.bootcamp.ecommerce.dto.customer.RegisterCustomerRequest;
import org.bootcamp.ecommerce.dto.customer.ResendActivationEmailRequest;
import org.bootcamp.ecommerce.dto.seller.RegisterSellerRequest;
import org.bootcamp.ecommerce.dto.seller.SellerResponse;
import org.bootcamp.ecommerce.entity.Customer;
import org.bootcamp.ecommerce.entity.Seller;
import org.bootcamp.ecommerce.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @PostMapping(
            value = "/register/customer",
            consumes = {"multipart/form-data"}
    )
    public ResponseEntity<CustomerResponseRegister> registerCustomer(
            @Valid @RequestPart("customer") RegisterCustomerRequest registerCustomerRequest,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        log.info("registerCustomer called with request: {}, image: {}", registerCustomerRequest, image != null);
        Customer customer = userService.registerCustomer(registerCustomerRequest, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CustomerResponseRegister(customer.getId(), customer.getEmail(),
                customer.getFirstName(), customer.getLastName()));
    }

    @PostMapping("/resend-activation-link")
    public ResponseEntity<String> resendActivationLink(@Valid @RequestBody ResendActivationEmailRequest resendActivationEmailRequest) {
        log.info("resendActivationLink called for email: {}", resendActivationEmailRequest.email());
        userService.resendActivationLink(resendActivationEmailRequest.email().trim());
        return ResponseEntity.ok("Email Sent Successfully");
    }

    @PostMapping(
            value = "/register/seller",
            consumes = {"multipart/form-data"}
    )
    public ResponseEntity<SellerResponse> registerSeller(@Valid @RequestPart("seller") RegisterSellerRequest registerSellerRequest,
                                                         @RequestPart(value = "image", required = false) MultipartFile image) {
        log.info("registerSeller called with request: {}, image: {}", registerSellerRequest, image != null);
        Seller seller = userService.registerSeller(registerSellerRequest, image);
        SellerResponse sellerResponse = new SellerResponse(seller.getEmail(), seller.getCompanyName(), seller.getGst(),
                seller.getFirstName(), seller.getLastName(), seller.getCompanyContact());
        return ResponseEntity.status(HttpStatus.CREATED).body(sellerResponse);
    }
}
