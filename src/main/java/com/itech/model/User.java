package com.itech.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Basic user class.
 * @author Edvard Krainiy on 12/3/2021
 */

@Entity
@Table(name="users")
@Getter
@Setter
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

    @Column(name="role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name="activated")
    private boolean activated;

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

