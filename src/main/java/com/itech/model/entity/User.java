package com.itech.model.entity;

import com.itech.model.enumeration.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * Basic user class.
 *
 * @author Edvard Krainiy on 12/3/2021
 */

@Entity
@Table(name = "users")
@Getter //TODO: use @Data annotation
@Setter
@NoArgsConstructor
public class User {
    private static final String VALID_EMAIL_ADDRESS_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    private static final String VALID_USERNAME_ADDRESS_REGEX = "^[a-zA-Z0-9._-]{3,}$";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull(message = "Username not found!")
    @Pattern(regexp = VALID_USERNAME_ADDRESS_REGEX, message = "Username is not valid!")
    @Column(name = "username")
    private String username;

    @NotNull(message = "Password not found!")
    @Column(name = "password")
    private String password;

    @NotNull(message = "Email not found!")
    @Pattern(regexp = VALID_EMAIL_ADDRESS_REGEX, message = "Email is not valid!")
    @Column(name = "email")
    private String email;

    @Column(name = "confirmation_token")
    private String confirmationToken;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "activated")
    private boolean activated;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private Set<Account> accounts;

    public User(String username, String password, String email, Role role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public User(String username, String password, String email, Role role, String confirmationToken) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.confirmationToken = confirmationToken;
    }
}

