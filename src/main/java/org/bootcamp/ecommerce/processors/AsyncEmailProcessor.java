package org.bootcamp.ecommerce.processors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bootcamp.ecommerce.entity.Product;
import org.bootcamp.ecommerce.entity.User;
import org.bootcamp.ecommerce.enums.RequestType;
import org.bootcamp.ecommerce.exception.UserNotFoundException;
import org.bootcamp.ecommerce.repository.UserRepo;
import org.bootcamp.ecommerce.services.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AsyncEmailProcessor {
    private final EmailService emailService;
    private final UserRepo userRepo;

    @Value("${user.admin.email}")
    String adminEmail;

    @Async
    public void emailSender(String email, String emailToken, RequestType requestType) {
        log.debug("Starting async emailSender for user email={}, requestType={}", email, requestType);
        if (email == null || requestType == null) {
            log.warn("Invalid inputs: user email={}, request email={}. Aborting email processing.", email, requestType);
            return;
        }
        email = email.trim();
        User user = userRepo.findByEmailIgnoreCase(email).orElseThrow(UserNotFoundException::new);
        try {
            sendEmailByRequestType(emailToken, requestType, user, null);
        } catch (Exception ex) {
            log.error("Failed to process async email for userId={}, requestType={}", user.getId(), requestType, ex);
        }
    }

    @Async
    public void sendActivationEmailToAdmin(Product product) {
        try {
            sendEmailByRequestType(null, RequestType.PRODUCT_CREATED_BY_SELLER, null, product);
        } catch (Exception ex) {
            log.error("Failed to process async email for product={}", product, ex);
        }
    }

    @Async
    public void activateProductEmail(Product product) {
        try {
            sendEmailByRequestType(null, RequestType.PRODUCT_ACTIVATED_BY_ADMIN, null, product);
        } catch (Exception ex) {
            log.error("Failed to process async email for product={}", product, ex);
        }
    }

    @Async
    public void deActiveProductEmail(Product product) {
        try {
            sendEmailByRequestType(null, RequestType.PRODUCT_DEACTIVATED_BY_ADMIN, null, product);
        } catch (Exception ex) {
            log.error("Failed to process async email for product={}", product, ex);
        }
    }


    private void sendEmailByRequestType(String emailToken, RequestType requestType, User user, Product product) {
        switch (requestType) {
            case NEW_TOKEN -> {
                emailService.sendEmail(user.getEmail(), "Activate your Bootcamp E-commerce account", buildActivationEmail(user.getFirstName(), emailToken, false));
                log.info("Sent new activation email to userId={}, email={}", user.getId(), user.getEmail());
            }
            case EXPIRED_TOKEN -> {
                emailService.sendEmail(user.getEmail(), "Your new activation link - Bootcamp E-commerce", buildActivationEmail(user.getFirstName(), emailToken, true));
                log.info("Sent reactivation email (expired token) to userId={}, email={}", user.getId(), user.getEmail());
            }
            case FORGOT_PASSWORD -> {
                emailService.sendEmail(user.getEmail(), "Reset your Bootcamp E-commerce password", buildForgotPasswordEmail(user.getFirstName(), emailToken));
                log.info("Sent forgot password email to userId={}, email={}", user.getId(), user.getEmail());
            }
            case ACTIVATED_ACCOUNT -> {
                emailService.sendEmail(user.getEmail(), "Your Bootcamp E-commerce account has been activated", buildActivatedEmail(user.getFirstName()));
                log.info("Sent activated account email to userId={}, email={}", user.getId(), user.getEmail());
            }
            case DEACTIVATED_ACCOUNT -> {
                emailService.sendEmail(user.getEmail(), "Your Bootcamp E-commerce account has been deactivated", buildDeActivatedEmail(user.getFirstName()));
                log.info("Sent deactivated account email to userId={}, email={}", user.getId(), user.getEmail());
            }
            case FORGOT_PASSWORD_SUCCESS -> {
                emailService.sendEmail(user.getEmail(), "Your Bootcamp E-commerce password has been reset successfully", buildForgotPasswordSuccessEmail(user.getFirstName()));
                log.info("Sent forgot password success email to userId={}, email={}", user.getId(), user.getEmail());
            }
            case PRODUCT_CREATED_BY_SELLER -> {
                emailService.sendEmail(adminEmail, "New Product Awaiting Activation: " + product.getName(), buildProductApprovalEmail(product));
                log.info("Sent product approval email to admin for product={}", product);
            }
            case PRODUCT_ACTIVATED_BY_ADMIN -> {
                emailService.sendEmail(product.getSeller().getEmail(), "Product Activated: " + product.getName(),
                        buildProductActivationEmail(product));
                log.info("Sent product activation email to admin for product={}", product);
            }
            case PRODUCT_DEACTIVATED_BY_ADMIN -> {
                emailService.sendEmail(product.getSeller().getEmail(), "Product Deactivated: " + product.getName(), buildProductDeActivationEmail(product));
                log.info("Sent product deactivation email to admin for product={}", product);
            }
            case ACCOUNT_LOCKED -> {
                emailService.sendEmail(user.getEmail(), "Your Bootcamp E-commerce account has been locked", buildAccountLockedEmail(user.getFirstName()));
                log.info("Sent account locked email to userId={}, email={}", user.getId(), user.getEmail());
            }
        }
    }

    private String buildForgotPasswordEmail(String username, String emailToken) {
        String greeting = "Hello " + username + "\n\n";
        String body = "We received a request to reset your Bootcamp E-commerce password. Please use the token below to reset your password:\n" + emailToken + "\n\n";
        String expiry = "This token will expire in 3 hours.\n\n";
        String footer = "If you didn’t request this, you can safely ignore this email.\n\nRegards,\nBootcamp E-commerce Team";
        return greeting + body + expiry + footer;
    }

    private String buildActivationEmail(String username, String emailToken, boolean isResend) {
        String greeting = "Hello " + username + "\n\n";
        String bodyNew = "Welcome to Bootcamp E-commerce!\n\nPlease use the token below to activate your account:\n" + emailToken + "\n\n";
        String bodyResend = "Your previous activation token expired. Here's a new one:\n" + emailToken + "\n\n";
        String expiry = "This token will expire in 3 hours.\n\n";
        String footer = "If you didn’t request this, you can safely ignore this email.\n\nRegards,\nBootcamp E-commerce Team";
        return greeting + (isResend ? bodyResend : bodyNew) + expiry + footer;
    }

    private String buildActivatedEmail(String username) {
        String greeting = "Hello " + username + "\n\n";
        String body = "Your Bootcamp E-commerce account has been activated.\n\n";
        String footer = "Regards,\nBootcamp E-commerce Team";
        return greeting + body + footer;
    }

    private String buildDeActivatedEmail(String username) {
        String greeting = "Hello " + username + "\n\n";
        String body = "Your Bootcamp E-commerce account has been deactivated.\n\n";
        String footer = "Regards,\nBootcamp E-commerce Team";
        return greeting + body + footer;
    }

    private String buildForgotPasswordSuccessEmail(String username) {
        String greeting = "Hello " + username + "\n\n";
        String body = "Your password has been reset successfully.\n\n";
        String footer = "Regards,\nBootcamp E-commerce Team";
        return greeting + body + footer;
    }

    private String buildProductApprovalEmail(Product product) {
        String greeting = "A new product has been added by seller " + product.getSeller().getEmail()
                + " and requires your approval.\n\n";
        String body = String.format("""
                        Product ID: %s
                        Product Name: %s
                        Brand: %s
                        Category: %s
                        
                        Please log in to the admin panel to activate it.""",
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getCategory().getName()
        );
        return greeting + body;
    }

    private String buildProductActivationEmail(Product product) {
        String greeting = "Your product " + product.getName() + " has been activated.\n\n";
        String body = String.format("""
                        Product ID: %s
                        Product Name: %s
                        Brand: %s
                        Category: %s
                        
                        """,
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getCategory().getName()
        );
        return greeting + body;
    }

    private String buildProductDeActivationEmail(Product product) {
        String greeting = "Your product " + product.getName() + " has been deactivated.\n\n";
        String body = String.format("""
                        Product ID: %s
                        Product Name: %s
                        Brand: %s
                        Category: %s
                        
                        """,
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getCategory().getName()
        );
        return greeting + body;
    }

    private String buildAccountLockedEmail(String username) {
        String greeting = "Hello " + username + "\n\n";
        String body = """
                Your Bootcamp E-commerce account has been locked due to multiple failed login attempts.
                 \
                Please contact support to unlock your account.
                
                """;
        String footer = "Regards,\nBootcamp E-commerce Team";
        return greeting + body + footer;
    }
}
