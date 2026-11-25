package org.bootcamp.ecommerce.repository;

import org.bootcamp.ecommerce.entity.User;
import org.bootcamp.ecommerce.enums.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {

    Optional<User> findByEmailToken(String emailToken);

    Optional<User> findByEmailIgnoreCase(String email);

    @Query("select (count(u) > 0) from User u join u.roles r where r.authority = :authority")
    boolean existsByRoleAuthority(@Param("authority") Authority authority);

    Optional<User> findByforgotPasswordToken(String forgotPasswordToken);

}
