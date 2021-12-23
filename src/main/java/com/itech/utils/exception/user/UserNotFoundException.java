package com.itech.utils.exception.user;

import com.itech.model.enumeration.Role;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * UserNotFoundException class.
 *
 * @author Edvard Krainiy on 12/12/2021
 */

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super(String.format("User with username = %s not found!", username));
    }

    public UserNotFoundException(Long userId) {
        super(String.format("User with id = %d not found!", userId));
    }

    public UserNotFoundException(Role role) {
        super(String.format("User with role = %s not found!", role));
    }
}
