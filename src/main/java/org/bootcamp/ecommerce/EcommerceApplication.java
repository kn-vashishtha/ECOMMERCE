package org.bootcamp.ecommerce;

import lombok.RequiredArgsConstructor;
import org.bootcamp.ecommerce.services.BootstrapService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableAsync
@RequiredArgsConstructor
@EnableScheduling
public class EcommerceApplication {

    private final BootstrapService bootstrapService;



    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            bootstrapService.bootstrapRoles();
            bootstrapService.bootstrapAdmin();
        };
    }

}
