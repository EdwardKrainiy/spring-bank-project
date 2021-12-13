package com.itech.utils.exception;

import com.itech.model.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * UserNotFoundException class.
 * @author Edvard Krainiy on 12/12/2021
 */

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String username){
        super("User with username = " + username + " not found!");
    }

    public UserNotFoundException(Long userId){
        super("User with id = " + userId + " not found!");
    }

    public UserNotFoundException(Role role){
        super("User with role = " + role + " not found!");
    }
}
