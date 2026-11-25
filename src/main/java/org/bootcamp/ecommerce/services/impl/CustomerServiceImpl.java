package org.bootcamp.ecommerce.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bootcamp.ecommerce.dto.address.AddressRequestCustomer;
import org.bootcamp.ecommerce.dto.address.AddressResponseDTO;
import org.bootcamp.ecommerce.dto.auth.GetAllResponse;
import org.bootcamp.ecommerce.dto.customer.CustomerListResponse;
import org.bootcamp.ecommerce.dto.customer.UpdateCustomerRequest;
import org.bootcamp.ecommerce.dto.customer.ViewCustomerProfileResponse;
import org.bootcamp.ecommerce.entity.Address;
import org.bootcamp.ecommerce.entity.Customer;
import org.bootcamp.ecommerce.exception.*;
import org.bootcamp.ecommerce.repository.AddressRepo;
import org.bootcamp.ecommerce.repository.CustomerRepo;
import org.bootcamp.ecommerce.repository.UserRepo;
import org.bootcamp.ecommerce.services.CustomerService;
import org.bootcamp.ecommerce.services.FileService;
import org.bootcamp.ecommerce.utils.mappers.Mappers;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepo customerRepo;
    private final UserRepo userRepo;
    private final FileService fileService;
    private final AddressRepo addressRepo;
    private final MessageSource messageSource;


    @Override
    public ResponseEntity<ViewCustomerProfileResponse> viewMyProfile(String email) {
        log.debug("viewMyProfile called with email: {}", email);
        Customer customer = (Customer) userRepo.findByEmailIgnoreCase(email).orElseThrow(UserNotFoundException::new);
        String customerImageFromFileSystem = fileService.getUserImageFromFileSystem(customer);
        return ResponseEntity.ok(new ViewCustomerProfileResponse(customer.getId().toString(), customer.getFirstName(), customer.getLastName(), customer.isActive(), customer.getContact(), customerImageFromFileSystem));
    }

    @Override
    public ResponseEntity<List<AddressResponseDTO>> viewAddresses(String email) {
        log.debug("viewAddresses called with email: {}", email);
        Customer customer = (Customer) userRepo.findByEmailIgnoreCase(email).orElseThrow(UserNotFoundException::new);
        List<Address> customerAddresses = customer.getAddresses();
        List<AddressResponseDTO> addressResponseDTOList = customerAddresses.stream().map(Mappers::addressDTOToAddressMapper).toList();
        return ResponseEntity.ok(addressResponseDTOList);
    }

    public ResponseEntity<GetAllResponse<CustomerListResponse>> getAllCustomers(Pageable pageable, String email) {
        log.debug("getAllCustomers called with email: {}", email);
        Page<Customer> page = customerRepo.findByEmailContainingIgnoreCase(email, pageable);
        GetAllResponse<CustomerListResponse> response = GetAllResponse.<CustomerListResponse>builder()
                .data(page.getContent().stream().map(this::buildCustomerListResponse).toList())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .lastPage(page.isLast()).build();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<String> addAddress(AddressRequestCustomer addressRequest, String name) {
        log.debug("addAddress called with name: {}", name);
        Customer customer = (Customer) userRepo.findByEmailIgnoreCase(name).orElseThrow(UserNotFoundException::new);
        Address address = Mappers.addressToCustomerAddressRequestMapper(addressRequest);
        customer.addAddress(address);
        customerRepo.save(customer);
        return ResponseEntity.ok(getMessage("address.added"));
    }

    @Override
    public ResponseEntity<String> deleteAddress(String id, String name) {
        log.debug("deleteAddress called with id: {}, name: {}", id, name);
        Address address = addressRepo.findById(UUID.fromString(id)).orElseThrow(AddressNotFoundException::new);
        addressRepo.delete(address);
        return ResponseEntity.ok(getMessage("address.deleted"));
    }

    private CustomerListResponse buildCustomerListResponse(Customer customer) {
        log.debug("buildCustomerListResponse called for customer id: {}", customer.getId());
        return new CustomerListResponse(customer.getId(),
                customer.getFirstName(), customer.getMiddleName(), customer.getLastName(),
                customer.getEmail(), customer.isActive()
        );
    }

    private void updateImageCustomer(MultipartFile image, Customer customer) {
        log.debug("updateImageCustomer called for customer id: {}", customer.getId());
        if (image == null) return;
        try {
            fileService.uploadUserImageToFileSystem(image, customer);
        } catch (IOException e) {
            throw new FileSavingException();
        }

    }

    @Override
    public ResponseEntity<String> updateProfile(UpdateCustomerRequest updateCustomerRequest, MultipartFile image,
                                                String email) {
        log.debug("updateProfile called with email: {}", email);
        Customer customer = (Customer) userRepo.findByEmailIgnoreCase(email).orElseThrow(UserNotFoundException::new);
        if (image != null) {
            fileService.checkValid(image);
        }
        updateCustomer(updateCustomerRequest, customer);
        updateImageCustomer(image, customer);
        customerRepo.save(customer);
        return ResponseEntity.ok(getMessage("profile.updated"));
    }

    private String getMessage(String key) {
        log.debug("getMessage called with key: {}", key);
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    private void updateCustomer(UpdateCustomerRequest updateCustomerRequest, Customer customer) {
        if (updateCustomerRequest == null) return;
        if (updateCustomerRequest.firstName() != null) {
            if (updateCustomerRequest.firstName().trim().isEmpty()) {
                throw new ValidationException("validation.firstname.blank");
            }
            customer.setFirstName(updateCustomerRequest.firstName().trim());
        }
        if (updateCustomerRequest.middleName() != null) {
            if (updateCustomerRequest.middleName().trim().isEmpty()) {
                throw new ValidationException("validation.middlename.blank");
            }
            customer.setMiddleName(updateCustomerRequest.middleName().trim());
        }
        if (updateCustomerRequest.lastName() != null) {
            if (updateCustomerRequest.lastName().trim().isEmpty()) {
                throw new ValidationException("validation.lastname.blank");
            }
            customer.setLastName(updateCustomerRequest.lastName().trim());
        }
        if (updateCustomerRequest.contactNumber() != null) {
            if (updateCustomerRequest.contactNumber().length() != 10) {
                throw new InvalidContactNumberException();
            }
            customer.setContact(updateCustomerRequest.contactNumber());
        }
    }
}
