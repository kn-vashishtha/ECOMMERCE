package org.bootcamp.ecommerce.services;

import org.bootcamp.ecommerce.dto.auth.GetAllResponse;
import org.bootcamp.ecommerce.dto.seller.SellerListResponse;
import org.bootcamp.ecommerce.dto.seller.UpdateSellerRequest;
import org.bootcamp.ecommerce.dto.seller.ViewSellerProfileResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface SellerService {
    ResponseEntity<GetAllResponse<SellerListResponse>> getAllSellers(Pageable pageable, String email);

    ResponseEntity<ViewSellerProfileResponse> viewMyProfile(String email);

    ResponseEntity<String> updateProfile(UpdateSellerRequest updateSellerRequest, MultipartFile image, String email);


}
