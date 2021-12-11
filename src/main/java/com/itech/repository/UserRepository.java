package com.itech.repository;

import com.itech.model.Role;
import com.itech.model.User;
import com.itech.utils.exception.UserNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA repository class.
 * @author Edvard Krainiy on ${date}
 * @version 1.0
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> getUserByUsername(String username) throws UserNotFoundException;
    Optional<User> getUserByEmail(String email) throws UserNotFoundException;
    Optional<User> getUserByRole(Role role) throws UserNotFoundException;
    Optional<User> getUserById(Long id) throws UserNotFoundException;

    /**
     * activateUser method.
     * @param userId id of user we want to activate.
     */
    default void activateUser(Long userId){
        try{
            User foundUser = getUserById(userId).orElseThrow(() -> new UserNotFoundException(userId));
            foundUser.setActivated(true);
            save(foundUser);
        }
        catch (UserNotFoundException exception){
            exception.getMessage();
        }
    }
}
