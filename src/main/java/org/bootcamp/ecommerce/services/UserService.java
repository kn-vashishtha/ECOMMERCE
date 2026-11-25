package org.bootcamp.ecommerce.services;

import org.bootcamp.ecommerce.dto.auth.ForgotPasswordRequest;
import org.bootcamp.ecommerce.dto.auth.ForgotPasswordTokenRequest;
import org.bootcamp.ecommerce.dto.customer.RegisterCustomerRequest;
import org.bootcamp.ecommerce.dto.seller.RegisterSellerRequest;
import org.bootcamp.ecommerce.dto.seller.UpdateAddress;
import org.bootcamp.ecommerce.dto.user.UpdatePassword;
import org.bootcamp.ecommerce.entity.Customer;
import org.bootcamp.ecommerce.entity.Seller;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;


public interface UserService {

    Customer registerCustomer(RegisterCustomerRequest registerCustomerRequest, MultipartFile file);

    Seller registerSeller(RegisterSellerRequest registerSellerRequest, MultipartFile image);

    ResponseEntity<String> verifyUserByEmailToken(String emailToken);

    void resendActivationLink(String email);

    ResponseEntity<String> forgotPassword(ForgotPasswordTokenRequest forgotPasswordTokenRequest);

    ResponseEntity<String> forgotPasswordUsingToken(String token, ForgotPasswordRequest forgotPasswordRequest);

    ResponseEntity<String> activateUser(String id);

    ResponseEntity<String> deactivateUser(String id);

    ResponseEntity<String> updateAddress(UpdateAddress updateAddress, Authentication authentication);

    ResponseEntity<String> updatePassword(UpdatePassword updatePassword, String email);

}
