package com.itech.model.entity;

import com.itech.model.enumeration.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

/**
 * Basic user class.
 *
 * @author Edvard Krainiy on 12/3/2021
 */

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

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

