package org.bootcamp.ecommerce.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bootcamp.ecommerce.dto.address.AddressRequestCustomer;
import org.bootcamp.ecommerce.dto.auth.ForgotPasswordRequest;
import org.bootcamp.ecommerce.dto.auth.ForgotPasswordTokenRequest;
import org.bootcamp.ecommerce.dto.customer.RegisterCustomerRequest;
import org.bootcamp.ecommerce.dto.seller.RegisterSellerRequest;
import org.bootcamp.ecommerce.dto.seller.UpdateAddress;
import org.bootcamp.ecommerce.dto.user.UpdatePassword;
import org.bootcamp.ecommerce.entity.*;
import org.bootcamp.ecommerce.enums.Authority;
import org.bootcamp.ecommerce.enums.RequestType;
import org.bootcamp.ecommerce.exception.*;
import org.bootcamp.ecommerce.processors.AsyncEmailProcessor;
import org.bootcamp.ecommerce.repository.AddressRepo;
import org.bootcamp.ecommerce.repository.RoleRepo;
import org.bootcamp.ecommerce.repository.SellerRepo;
import org.bootcamp.ecommerce.repository.UserRepo;
import org.bootcamp.ecommerce.services.FileService;
import org.bootcamp.ecommerce.services.UserService;
import org.bootcamp.ecommerce.utils.mappers.Mappers;
import org.bootcamp.ecommerce.utils.validators.EmailValidator;
import org.bootcamp.ecommerce.utils.validators.GstValidator;
import org.bootcamp.ecommerce.utils.validators.PasswordValidator;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final SellerRepo sellerRepo;
    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    private final AddressRepo addressRepo;
    private final AsyncEmailProcessor asyncEmailProcessor;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final FileService fileService;
    private final MessageSource messageSource;

    public Customer registerCustomer(RegisterCustomerRequest registerCustomerRequest, MultipartFile image) {
        log.debug("registerCustomer called with email: {}, image: {}", registerCustomerRequest.email(), image != null);
        EmailValidator.validateEmail(registerCustomerRequest.email());
        validateDuplicateEmail(registerCustomerRequest.email());
        Customer customer = buildCustomer(registerCustomerRequest);
        String emailToken = assignCustomerRole(customer, registerCustomerRequest);
        if (image != null) {
            fileService.checkValid(image);
        }
        userRepo.save(customer);
        if (image != null) {
            try {
                fileService.uploadUserImageToFileSystem(image, customer);
            } catch (IOException e) {
                throw new FileSavingException();
            }
        }
        asyncEmailProcessor.emailSender(registerCustomerRequest.email(), emailToken, RequestType.NEW_TOKEN);
        return customer;
    }

    public Seller registerSeller(RegisterSellerRequest registerSellerRequest, MultipartFile image) {
        log.debug("registerSeller called with email: {}, gst: {}, companyName: {}, image: {}", registerSellerRequest.email(), registerSellerRequest.gst(), registerSellerRequest.companyName(), image != null);
        EmailValidator.validateEmail(registerSellerRequest.email());
        validateDuplicateEmail(registerSellerRequest.email());
        GstValidator.validateGst(registerSellerRequest.gst());
        validateDuplicateGst(registerSellerRequest.gst());
        validateDuplicateCompanyName(registerSellerRequest.companyName());
        if (image != null) {
            fileService.checkValid(image);
        }
        Seller seller = buildSeller(registerSellerRequest);
        assignSellerRole(seller, registerSellerRequest);
        sellerRepo.save(seller);
        if (image != null) {
            try {
                fileService.uploadUserImageToFileSystem(image, seller);
            } catch (IOException e) {
                throw new FileSavingException();
            }
        }
        return seller;
    }

    public ResponseEntity<String> verifyUserByEmailToken(String emailToken) {
        log.debug("verifyUserByEmailToken called with token: {}", emailToken);
        User userByEmailToken = userRepo.findByEmailToken(emailToken.trim()).orElse(null);
        if (userByEmailToken == null) {
            return ResponseEntity.badRequest().body(getMessage("user.token.invalid"));
        }

        if (userByEmailToken.isEmailTokenExpired()) {
            handleExpiredEmailToken(userByEmailToken);
            return ResponseEntity.badRequest().body(getMessage("user.token.expired"));
        }
        userByEmailToken.setActive(true);
        userRepo.save(userByEmailToken);
        asyncEmailProcessor.emailSender(userByEmailToken.getEmail(), null, RequestType.ACTIVATED_ACCOUNT);
        return ResponseEntity.ok(getMessage("user.email.verified"));
    }


    public void resendActivationLink(String email) {
        log.debug("resendActivationLink called with email: {}", email);
        EmailValidator.validateEmail(email.trim());
        User userByEmail = userRepo.findByEmailIgnoreCase(email.trim()).orElseThrow(EmailNotFoundException::new);
        if (userByEmail.isActive()) {
            throw new EmailAlreadyVerifiedException();
        }
        String newEmailToken = assignNewEmailToken(userByEmail);
        userRepo.save(userByEmail);
        asyncEmailProcessor.emailSender(userByEmail.getEmail(), newEmailToken, RequestType.NEW_TOKEN);
    }

    public ResponseEntity<String> forgotPassword(ForgotPasswordTokenRequest forgotPasswordTokenRequest) {
        log.debug("forgotPassword called with email: {}", forgotPasswordTokenRequest.email());
        EmailValidator.validateEmail(forgotPasswordTokenRequest.email());
        User userByEmail = userRepo.findByEmailIgnoreCase(forgotPasswordTokenRequest.email().trim()).orElseThrow(EmailNotFoundException::new);
        if (!userByEmail.isActive()) {
            return ResponseEntity.badRequest().body(getMessage("user.not.activated"));
        }
        assignForgotPasswordToken(userByEmail);
        return ResponseEntity.ok(getMessage("user.password.reset.link"));
    }

    public ResponseEntity<String> forgotPasswordUsingToken(String token, ForgotPasswordRequest forgotPasswordRequest) {
        User user = userRepo.findByforgotPasswordToken(token).orElseThrow(InvalidTokenException::new);
        log.debug("forgotPasswordUsingToken called with token: {}, email: {}", token, user.getEmail());
        if (!isValidForgotPasswordToken(user, token)) {
            return ResponseEntity.badRequest().body(getMessage("user.token.invalid"));
        }
        if (user.isPasswordTokenExpired()) {
            throw new TokenExpiredException();
        }
        resetPassword(user, forgotPasswordRequest);
        return ResponseEntity.ok(getMessage("user.password.reset.success"));
    }

    @Override
    public ResponseEntity<String> activateUser(String id) {
        log.debug("activateUser called with id: {}", id);
        User user = userRepo.findById(UUID.fromString(id)).orElseThrow(UserNotFoundException::new);
        if (user.isActive()) {
            return ResponseEntity.ok(getMessage("user.already.activated"));
        }
        user.setActive(true);
        userRepo.save(user);
        if (user instanceof Customer) {
            asyncEmailProcessor.emailSender(user.getEmail(), null, RequestType.ACTIVATED_ACCOUNT);
            return ResponseEntity.ok(getMessage("user.customer.activated"));
        }
        if (user instanceof Seller) {
            asyncEmailProcessor.emailSender(user.getEmail(), null, RequestType.ACTIVATED_ACCOUNT);
        }
        return ResponseEntity.ok(getMessage("user.seller.activated"));
    }

    @Override
    public ResponseEntity<String> deactivateUser(String id) {
        log.debug("deactivateUser called with id: {}", id);
        User user = userRepo.findById(UUID.fromString(id)).orElseThrow(UserNotFoundException::new);
        if (!user.isActive()) {
            return ResponseEntity.ok(getMessage("user.already.deactivated"));
        }
        user.setActive(false);
        userRepo.save(user);
        if (user.getRoles().stream().anyMatch(role -> role.getAuthority().equals(Authority.ADMIN))) {
            return ResponseEntity.ok(getMessage("user.admin.cannot.deactivate"));
        }
        if (user instanceof Customer) {
            asyncEmailProcessor.emailSender(user.getEmail(), null, RequestType.DEACTIVATED_ACCOUNT);
            return ResponseEntity.ok(getMessage("user.customer.deactivated"));
        }
        if (user instanceof Seller) {
            asyncEmailProcessor.emailSender(user.getEmail(), null, RequestType.DEACTIVATED_ACCOUNT);
        }
        return ResponseEntity.ok(getMessage("user.seller.deactivated"));
    }

    @Override
    public ResponseEntity<String> updateAddress(UpdateAddress updateAddress, Authentication authentication) {
        log.debug("updateAddress called with addressId: {}, authentication: {}", updateAddress.id(), authentication != null ? authentication.getName() : null);
        Address address = addressRepo.findById(UUID.fromString(updateAddress.id())).orElseThrow(AddressNotFoundException::new);
        if (updateAddress.city() != null) {
            address.setCity(updateAddress.city());
        }
        if (updateAddress.state() != null) {
            address.setState(updateAddress.state());
        }
        if (updateAddress.country() != null) {
            address.setCountry(updateAddress.country());
        }
        if (updateAddress.addressLine() != null) {
            address.setAddressLine(updateAddress.addressLine());
        }
        if (updateAddress.zipCode() != null) {
            address.setZipCode(updateAddress.zipCode());
        }
        addressRepo.save(address);
        return ResponseEntity.ok(getMessage("user.address.updated"));
    }

    @Override
    public ResponseEntity<String> updatePassword(UpdatePassword updatePassword, String email) {
        log.debug("updatePassword called with email: {}", email);
        User user = userRepo.findByEmailIgnoreCase(email).orElseThrow(UserNotFoundException::new);
        String password = PasswordValidator.validateAndReturnPassword(updatePassword.password(), updatePassword.confirmPassword());
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepo.save(user);
        asyncEmailProcessor.emailSender(email, null, RequestType.FORGOT_PASSWORD_SUCCESS);
        return ResponseEntity.ok(getMessage("user.password.updated"));
    }

    private void resetPassword(User user, ForgotPasswordRequest forgotPasswordRequest) {
        String password = PasswordValidator.validateAndReturnPassword(forgotPasswordRequest.password(), forgotPasswordRequest.confirmPassword());
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setForgotPasswordToken(null);
        user.setPasswordUpdateDate(LocalDate.now());
        user.setLocked(false);
        user.setInValidAttemptCount(0);
        asyncEmailProcessor.emailSender(user.getEmail(), null, RequestType.FORGOT_PASSWORD_SUCCESS);
        userRepo.save(user);
    }

    private Customer buildCustomer(RegisterCustomerRequest request) {
        String password = PasswordValidator.validateAndReturnPassword(request.password(), request.confirmPassword());
        return Customer.builder()
                .email(request.email().trim().toLowerCase())
                .firstName(request.firstName().trim())
                .middleName(request.middleName() != null ? request.middleName() : "")
                .lastName(request.lastName().trim())
                .contact(request.contactNumber())
                .password(bCryptPasswordEncoder.encode(password.trim()))
                .passwordUpdateDate(LocalDate.now())
                .build();
    }

    private String assignCustomerRole(Customer customer, RegisterCustomerRequest registerCustomerRequest) {
        Role role = roleRepo.findByAuthority(Authority.CUSTOMER).orElseGet(() -> {
            Role newRole = new Role();
            newRole.setAuthority(Authority.CUSTOMER);
            return roleRepo.save(newRole);
        });
        for (AddressRequestCustomer addressRequest : registerCustomerRequest.address()) {
            customer.addAddress(Mappers.addressToCustomerAddressRequestMapper(addressRequest));
        }
        customer.addRole(role);
        String token = UUID.randomUUID().toString();
        customer.setEmailToken(token);
        customer.setEmailTokenExpiryDate(LocalDateTime.now().plusHours(3));
//       customer.setEmailTokenExpiryDate(LocalDateTime.now().plusMinutes(1));
        return token;
    }

    private Seller buildSeller(RegisterSellerRequest request) {
        String password = PasswordValidator.validateAndReturnPassword(request.password(), request.confirmPassword());
        return Seller.builder()
                .email(request.email().trim().toLowerCase())
                .firstName(request.firstName().trim())
                .middleName(request.middleName() != null ? request.middleName() : "")
                .lastName(request.lastName().trim())
                .password(bCryptPasswordEncoder.encode(password.trim()))
                .passwordUpdateDate(LocalDate.now())
                .companyContact(request.companyContactNumber())
                .companyName(request.companyName())
                .gst(request.gst())
                .build();
    }

    private void assignSellerRole(Seller seller, RegisterSellerRequest request) {
        Role role = roleRepo.findByAuthority(Authority.SELLER).orElseGet(() -> {
            Role newRole = new Role();
            newRole.setAuthority(Authority.SELLER);
            return roleRepo.save(newRole);
        });
        seller.addAddress(Mappers.addressToSellerAddressRequestMapper(request.address()));
        seller.addRole(role);
    }

    private void handleExpiredEmailToken(User user) {
        String newToken = UUID.randomUUID().toString();
        user.setEmailToken(newToken);
        user.setEmailTokenExpiryDate(LocalDateTime.now().plusHours(3));
        userRepo.save(user);
        asyncEmailProcessor.emailSender(user.getEmail(), newToken, RequestType.EXPIRED_TOKEN);
    }

    private String assignNewEmailToken(User user) {
        String token = UUID.randomUUID().toString();
        user.setEmailToken(token);
        user.setEmailTokenExpiryDate(LocalDateTime.now().plusHours(3));
        return token;
    }

    private void validateDuplicateEmail(String email) {
        if (userRepo.findByEmailIgnoreCase((email)).isPresent()) {
            throw new DuplicateEmailException();
        }
    }

    private void validateDuplicateGst(String gst) {
        if (sellerRepo.existsByGstIgnoreCase(gst)) {
            throw new DuplicateGstException();
        }
    }

    private void validateDuplicateCompanyName(String companyName) {
        if (sellerRepo.existsByCompanyNameIgnoreCase(companyName)) {
            throw new DuplicateCompanyNameException();
        }
    }

    private void assignForgotPasswordToken(User userByEmail) {
        String forgotPasswordToken = UUID.randomUUID().toString();
        userByEmail.setForgotPasswordToken(forgotPasswordToken);
        userByEmail.setForgotPasswordTokenExpiryDate(LocalDateTime.now().plusHours(3));
        userRepo.save(userByEmail);
        asyncEmailProcessor.emailSender(userByEmail.getEmail(), forgotPasswordToken, RequestType.FORGOT_PASSWORD);
    }

    private boolean isValidForgotPasswordToken(User user, String token) {
        return user.getForgotPasswordToken() != null && user.getForgotPasswordToken().equals(token);
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}

