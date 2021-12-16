package com.itech.service.user;

import com.itech.model.User;
import com.itech.model.dto.UserDto;
import com.itech.utils.exception.*;
import org.springframework.http.ResponseEntity;

/**
 * UserService interface. Provides us different methods to work with User objects on Service layer.
 * @author Edvard Krainiy on 12/7/2021
 */

public interface UserService {
    ResponseEntity<Void> createUser(UserDto userDto) throws UserNotFoundException, UserExistsException, UserValidationException;

    User findUserByUsername(String username) throws UserNotFoundException;

    User findUserByUsernameAndPassword(String username, String password) throws UserNotFoundException;

    ResponseEntity<Void> activateUser(String token);
}
