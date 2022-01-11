package com.itech.repository;

import com.itech.model.entity.User;
import com.itech.model.enumeration.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA User repository  class.
 *
 * @author Edvard Krainiy on 12/8/2021
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> getUserByUsername(String username); // TODO: rename get to find

    Optional<User> getUserByEmail(String email); // TODO: rename get to find

    Optional<User> getUserByRole(Role role); // TODO: rename get to find

    Optional<User> getUserById(Long id); // TODO: already exists
}
