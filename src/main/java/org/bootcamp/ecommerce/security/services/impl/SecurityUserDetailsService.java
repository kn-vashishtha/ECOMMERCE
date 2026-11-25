package org.bootcamp.ecommerce.security.services.impl;

import lombok.RequiredArgsConstructor;
import org.bootcamp.ecommerce.repository.UserRepo;
import org.bootcamp.ecommerce.security.UserPrinciple;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {
    final UserRepo userRepo;

    @Override
    public UserPrinciple loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserPrinciple(
                userRepo.findByEmailIgnoreCase(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username)));
    }

}
