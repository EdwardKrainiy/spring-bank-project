package com.itech.model.entity;

import com.itech.model.enumeration.Role;
import com.itech.utils.literal.JpaMappingDetails;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

/**
 * Basic user class.
 *
 * @author Edvard Krainiy on 12/3/2021
 */

@Entity
@Getter
@Setter
@Table(name = JpaMappingDetails.USERS_TABLE)
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = JpaMappingDetails.ID, nullable = false)
    private Long id;

    @Column(name = JpaMappingDetails.USERNAME)
    private String username;

    @Column(name = JpaMappingDetails.PASSWORD)
    private String password;

    @Column(name = JpaMappingDetails.EMAIL)
    private String email;

    @Column(name = JpaMappingDetails.CONFIRMATION_TOKEN)
    private String confirmationToken;

    @Column(name = JpaMappingDetails.ROLE)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = JpaMappingDetails.ACTIVATED)
    private boolean activated;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = JpaMappingDetails.USER)
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

