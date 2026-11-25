package org.bootcamp.ecommerce.services;

import jakarta.validation.Valid;
import org.bootcamp.ecommerce.dto.address.AddressRequestCustomer;
import org.bootcamp.ecommerce.dto.address.AddressResponseDTO;
import org.bootcamp.ecommerce.dto.auth.GetAllResponse;
import org.bootcamp.ecommerce.dto.customer.CustomerListResponse;
import org.bootcamp.ecommerce.dto.customer.UpdateCustomerRequest;
import org.bootcamp.ecommerce.dto.customer.ViewCustomerProfileResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerService {
    ResponseEntity<GetAllResponse<CustomerListResponse>> getAllCustomers(Pageable pageable, String email);

    ResponseEntity<ViewCustomerProfileResponse> viewMyProfile(String email);

    ResponseEntity<List<AddressResponseDTO>> viewAddresses(String email);

    ResponseEntity<String> updateProfile(UpdateCustomerRequest updateCustomerRequest, MultipartFile image,
                                         String email);

    ResponseEntity<String> addAddress(@Valid AddressRequestCustomer addressRequest, String name);

    ResponseEntity<String> deleteAddress(String id, String name);
}
