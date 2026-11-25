package org.bootcamp.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "middle_name")
    private String middleName;
    @Column(name = "last_name")
    private String lastName;
    private String password;
    @Column(name = "is_deleted")
    @Builder.Default
    private boolean isDeleted = false;
    @Builder.Default
    @Column(name = "is_active")
    private boolean isActive = false;
    @Builder.Default
    @Column(name = "is_expired")
    private boolean isExpired = false;
    @Builder.Default
    @Column(name = "is_locked")
    private boolean isLocked = false;
    @Column(name = "invalid_attempt_count")
    @Builder.Default
    private int inValidAttemptCount = 0;
    @Column(name = "password_update_date")
    private LocalDate passwordUpdateDate;
    @Column(name = "email_token")
    private String emailToken;
    @Column(name = "email_token_expiry_date")
    private LocalDateTime emailTokenExpiryDate;
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.REFRESH}, mappedBy = "users", fetch = FetchType.EAGER)
    private List<Role> roles;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private List<Address> addresses;
    @Column(name = "forgot_password_token")
    private String forgotPasswordToken;
    @Column(name = "forgot_password_token_expiry_date")
    private LocalDateTime forgotPasswordTokenExpiryDate;


    public void addAddress(Address address) {
        if (addresses == null) {
            addresses = new ArrayList<>();
        }
        addresses.add(address);
    }

    public void addRole(Role role) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        roles.add(role);
        role.addUser(this);
    }

    @Transient
    public boolean isEmailTokenExpired() {
        return LocalDateTime.now().isAfter(emailTokenExpiryDate);
    }

    @Transient
    public boolean isPasswordTokenExpired() {
        return LocalDateTime.now().isAfter(forgotPasswordTokenExpiryDate);
    }

}
