package org.bootcamp.ecommerce.services;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
