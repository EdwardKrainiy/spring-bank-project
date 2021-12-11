package com.itech.service.user;

import com.itech.model.User;
import com.itech.model.dto.UserDto;
import com.itech.utils.exception.*;
import org.springframework.http.ResponseEntity;

/**
 * UserService interface. Provides us different methods to work with User objects on Service layer.
 * @author Edvard Krainiy on ${date}
 * @version 1.0
 */

public interface UserService {
    ResponseEntity createUser(UserDto userDto) throws EmptyUsernameException, UserNotFoundException, EmptyPasswordException, EmptyEmailException, InvalidEmailException, UserExistsException, IncorrectPasswordLengthException;

    User findUserByUsername(String username) throws UserNotFoundException;

    User findUserByUsernameAndPassword(String username, String password) throws UserNotFoundException;

    ResponseEntity activateUser(String token);
}
