package org.bootcamp.ecommerce.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bootcamp.ecommerce.dto.auth.GetAllResponse;
import org.bootcamp.ecommerce.dto.customer.CustomerListResponse;
import org.bootcamp.ecommerce.dto.seller.SellerListResponse;
import org.bootcamp.ecommerce.services.CustomerService;
import org.bootcamp.ecommerce.services.UserService;
import org.bootcamp.ecommerce.services.impl.SellerServiceImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final CustomerService customerService;
    private final SellerServiceImpl sellerService;
    private final UserService userService;

    @GetMapping("/get-all-customers")
    public ResponseEntity<GetAllResponse<CustomerListResponse>> getAllCustomers(@PageableDefault(sort = "id") Pageable pageable, @RequestParam(name = "email", required = false, defaultValue = "") String email) {
        log.info("Admin request to get all customers. Filtering by email: '{}'", email);
        return customerService.getAllCustomers(pageable, email);
    }

    @GetMapping("/get-all-sellers")
    public ResponseEntity<GetAllResponse<SellerListResponse>> getAllSellers(@PageableDefault(sort = "id") Pageable pageable, @RequestParam(name = "email", required = false, defaultValue = "") String email) {
        log.info("Admin request to get all sellers. Filtering by email: '{}'", email);
        return sellerService.getAllSellers(pageable, email);
    }

    @PatchMapping("/activate-user/{id}")
    public ResponseEntity<String> activateUser(@PathVariable("id") String id) {
        log.info("Admin request to ACTIVATE user with ID: {}", id);
        ResponseEntity<String> response = userService.activateUser(id);
        log.info("Successfully processed activation request for user ID: {}", id);
        return response;
    }

    @PatchMapping("/deactivate-user/{id}")
    public ResponseEntity<String> deactivateUser(@PathVariable("id") String id) {
        log.info("Admin request to DEACTIVATE user with ID: {}", id);
        ResponseEntity<String> response = userService.deactivateUser(id);
        log.info("Successfully processed deactivation request for user ID: {}", id);
        return response;
    }
}
