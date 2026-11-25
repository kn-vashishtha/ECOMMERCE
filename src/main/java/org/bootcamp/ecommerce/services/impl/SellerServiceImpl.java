package org.bootcamp.ecommerce.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bootcamp.ecommerce.dto.address.AddressResponseDTO;
import org.bootcamp.ecommerce.dto.auth.GetAllResponse;
import org.bootcamp.ecommerce.dto.seller.SellerListResponse;
import org.bootcamp.ecommerce.dto.seller.UpdateSellerRequest;
import org.bootcamp.ecommerce.dto.seller.ViewSellerProfileResponse;
import org.bootcamp.ecommerce.entity.Address;
import org.bootcamp.ecommerce.entity.Seller;
import org.bootcamp.ecommerce.exception.*;
import org.bootcamp.ecommerce.repository.SellerRepo;
import org.bootcamp.ecommerce.repository.UserRepo;
import org.bootcamp.ecommerce.services.FileService;
import org.bootcamp.ecommerce.services.SellerService;
import org.bootcamp.ecommerce.utils.MessageUtil;
import org.bootcamp.ecommerce.utils.mappers.Mappers;
import org.bootcamp.ecommerce.utils.validators.GstValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
@RequiredArgsConstructor
@Slf4j
public class SellerServiceImpl implements SellerService {
    private final SellerRepo sellerRepo;
    private final FileService fileService;
    private final UserRepo userRepository;

    @Override
    public ResponseEntity<GetAllResponse<SellerListResponse>> getAllSellers(Pageable pageable, String email) {
        log.debug("getAllSellers called with email: {}", email);
        Page<Seller> page = sellerRepo.findByEmailContainingIgnoreCase(email, pageable);
        GetAllResponse<SellerListResponse> response = GetAllResponse.<SellerListResponse>builder()
                .data(page.getContent().stream().map(this::buildSellerListResponse).toList())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .lastPage(page.isLast()).build();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ViewSellerProfileResponse> viewMyProfile(String email) {
        log.debug("viewMyProfile called with email: {}", email);
        Seller seller = (Seller) userRepository.findByEmailIgnoreCase(email).orElseThrow(UserNotFoundException::new);
        String userImageFromFileSystem = fileService.getUserImageFromFileSystem(seller);
        final AddressResponseDTO addressResponseDTO = Mappers.addressDTOToAddressMapper(seller.getAddresses().get(0));
        return ResponseEntity.ok(new ViewSellerProfileResponse(seller.getId().toString(), seller.getFirstName(), seller.getLastName(), seller.isActive(), seller.getCompanyContact(), seller.getCompanyName(), userImageFromFileSystem, seller.getGst(), addressResponseDTO));
    }

    private SellerListResponse buildSellerListResponse(Seller seller) {
        log.debug("buildSellerListResponse called for seller id: {}", seller.getId());
        Address address = seller.getAddresses().get(0);
        AddressResponseDTO addressResponseDTO = Mappers.addressDTOToAddressMapper(address);
        return new SellerListResponse(seller.getId(),
                seller.getFirstName(), seller.getMiddleName(), seller.getLastName(),
                seller.getEmail(), seller.isActive(), seller.getCompanyName(), addressResponseDTO, seller.getCompanyContact()
        );
    }

    @Override
    public ResponseEntity<String> updateProfile(UpdateSellerRequest updateSellerRequest, MultipartFile image,
                                                String email) {
        log.debug("updateProfile called with email: {}", email);
        Seller seller = (Seller) userRepository.findByEmailIgnoreCase(email).orElseThrow(UserNotFoundException::new);
        if (image != null) {
            fileService.checkValid(image);
        }
        updateSeller(updateSellerRequest, seller);
        updateImageSeller(image, seller);
        sellerRepo.save(seller);
        return ResponseEntity.ok(MessageUtil.getMessage("seller.profile.updated"));
    }

    private void updateImageSeller(MultipartFile image, Seller seller) {
        log.debug("updateImageSeller called for seller id: {}", seller.getId());
        if (image == null) return;
        try {
            fileService.uploadUserImageToFileSystem(image, seller);
        } catch (IOException e) {
            throw new FileSavingException();
        }
    }

    private void updateSeller(UpdateSellerRequest updateSellerRequest, Seller seller) {
        log.debug("updateSeller called for seller id: {}", seller.getId());
        if (updateSellerRequest == null) return;
        if (updateSellerRequest.firstName() != null) {
            if (updateSellerRequest.firstName().trim().isEmpty()) {
                throw new ValidationException("validation.firstname.blank");
            }
            seller.setFirstName(updateSellerRequest.firstName().trim());
        }
        if (updateSellerRequest.middleName() != null) {
            if (updateSellerRequest.middleName().trim().isEmpty()) {
                throw new ValidationException("validation.middlename.blank");
            }
            seller.setMiddleName(updateSellerRequest.middleName().trim());
        }
        if (updateSellerRequest.lastName() != null) {
            if (updateSellerRequest.lastName().trim().isEmpty()) {
                throw new ValidationException("validation.lastname.blank");
            }
            seller.setLastName(updateSellerRequest.lastName().trim());
        }
        if (updateSellerRequest.companyName() != null) {
            if (updateSellerRequest.companyName().trim().isEmpty()) {
                throw new ValidationException("validation.seller.companyname.blank");
            }
            if (sellerRepo.existsByCompanyNameIgnoreCase(updateSellerRequest.companyName().trim())) {
                throw new DuplicateCompanyNameException();
            }
            seller.setCompanyName(updateSellerRequest.companyName().trim());
        }
        if (updateSellerRequest.gst() != null) {
            GstValidator.validateGst(updateSellerRequest.gst());
            if (sellerRepo.existsByGstIgnoreCase(updateSellerRequest.gst())) {
                throw new DuplicateGstException();
            }
            seller.setGst(updateSellerRequest.gst());
        }
        if (updateSellerRequest.companyContactNumber() != null) {
            if (updateSellerRequest.companyContactNumber().length() != 10) {
                throw new InvalidContactNumberException();
            }
            seller.setCompanyContact(updateSellerRequest.companyContactNumber());
        }
    }

}
