package org.bootcamp.ecommerce.utils.validators;

import org.bootcamp.ecommerce.security.UserPrinciple;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

public class UserPrincipleValidator {
    private UserPrincipleValidator() {
    }

    public static void checkValidUserPrinciple(UserPrinciple userPrinciple) {
        if (!userPrinciple.isAccountNonLocked()) {
            throw new LockedException("User account is locked");
        }
        if (!userPrinciple.isEnabled()) {
            throw new DisabledException("User account is disabled");
        }
        if (!userPrinciple.isAccountNonExpired()) {
            throw new AccountExpiredException("User account is expired");
        }
        if (!userPrinciple.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("User credentials are expired");
        }

    }
}
