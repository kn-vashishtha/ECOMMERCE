package org.bootcamp.ecommerce.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bootcamp.ecommerce.entity.Role;
import org.bootcamp.ecommerce.entity.User;
import org.bootcamp.ecommerce.enums.Authority;
import org.bootcamp.ecommerce.repository.RoleRepo;
import org.bootcamp.ecommerce.repository.UserRepo;
import org.bootcamp.ecommerce.services.BootstrapService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class BootstrapServiceImpl implements BootstrapService {
    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    @Value("${user.admin.email}")
    String adminEmail;
    @Value("${user.admin.password}")
    String adminPassword;

    @Transactional
    public void bootstrapRoles() {
        log.debug("Bootstrapping roles...");
        boolean customerExists = roleRepo.existsByAuthority(Authority.CUSTOMER);
        log.debug("Customer role exists: {}", customerExists);
        if (!customerExists) {
            log.debug("Creating CUSTOMER role");
            Role newRole = new Role();
            newRole.setAuthority(Authority.CUSTOMER);
            roleRepo.save(newRole);
            log.debug("CUSTOMER role created");
        }
        boolean sellerExists = roleRepo.existsByAuthority(Authority.SELLER);
        log.debug("Seller role exists: {}", sellerExists);
        if (!sellerExists) {
            log.debug("Creating SELLER role");
            Role newRole = new Role();
            newRole.setAuthority(Authority.SELLER);
            roleRepo.save(newRole);
            log.debug("SELLER role created");
        }
        boolean adminExists = roleRepo.existsByAuthority(Authority.ADMIN);
        log.debug("Admin role exists: {}", adminExists);
        if (!adminExists) {
            log.debug("Creating ADMIN role");
            Role newRole = new Role();
            newRole.setAuthority(Authority.ADMIN);
            roleRepo.save(newRole);
            log.debug("ADMIN role created");
        }
        log.debug("Role bootstrapping complete");
    }

    @Transactional
    public void bootstrapAdmin() {
        log.debug("Bootstrapping admin user...");
        boolean adminExists = userRepo.existsByRoleAuthority(Authority.ADMIN);
        log.debug("Admin user exists: {}", adminExists);
        Role adminRole = roleRepo.findByAuthority(Authority.ADMIN)
                .orElseThrow(() -> {
                    log.debug("ADMIN role not found. Throwing exception.");
                    return new IllegalStateException("ADMIN role not found. Run bootstrapRoles first.");
                });
        if (!adminExists) {
            log.debug("Creating admin user with email: {}", adminEmail);
            User user = new User();
            user.setEmail(adminEmail);
            user.setPassword(passwordEncoder.encode(adminPassword));
            user.setPasswordUpdateDate(LocalDate.now());
            user.setFirstName("Admin");
            user.setLastName("Admin");
            user.setActive(true);
            user.addRole(adminRole);
            userRepo.save(user);
            log.debug("Admin user created");
        }
        log.debug("Admin bootstrapping complete");
    }
}
