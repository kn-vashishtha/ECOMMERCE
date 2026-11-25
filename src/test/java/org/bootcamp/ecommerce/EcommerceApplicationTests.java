package org.bootcamp.ecommerce;

import org.bootcamp.ecommerce.services.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EcommerceApplicationTests {
    /*
    Please Ignore
    Sonar qube
     */
    @Test
    void contextLoads() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Autowired
    EmailService emailService;

    @Test
    void sendEmailTest() {
        emailService.sendEmail("ecommerce.bootcamp.ayushtomer@gmail.com", "Test Email", "This is a test email");
    }

}
