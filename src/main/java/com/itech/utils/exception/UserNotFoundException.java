package com.itech.utils.exception;

import com.itech.model.Role;
import org.springframework.http.ResponseEntity;

/**
 * UserNotFoundException class.
 * @author Edvard Krainiy on ${date}
 * @version 1.0
 */

public class UserNotFoundException extends Exception{
    public UserNotFoundException(String username){
        super("User with username = " + username + " not found!");
        ResponseEntity.status(404).body("User with username = " + username + " not found!");
    }

    public UserNotFoundException(Long userId){
        super("User with id = " + userId + " not found!");
        ResponseEntity.status(404).body("User with id = " + userId + " not found!");
    }

    public UserNotFoundException(Role role){
        super("User with role = " + role + " not found!");
        ResponseEntity.status(404).body("User with role = " + role + " not found!");
    }
}
