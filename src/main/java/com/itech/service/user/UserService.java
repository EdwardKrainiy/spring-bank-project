package com.itech.service.user;

import com.itech.model.entity.User;
import com.itech.model.dto.user.UserDto;
import com.itech.utils.exception.EntityExistsException;
import com.itech.utils.exception.EntityIdNotFoundException;
import com.itech.utils.exception.EntityNotFoundException;
import com.itech.utils.exception.EntityValidationException;
import org.springframework.http.ResponseEntity;

/**
 * UserService interface. Provides us different methods to work with User objects on Service layer.
 *
 * @author Edvard Krainiy on 12/7/2021
 */

public interface UserService {
    ResponseEntity<Void> createUser(UserDto userDto) throws EntityIdNotFoundException, EntityExistsException, EntityValidationException;

    User findUserByUsername(String username) throws EntityNotFoundException;

    User findUserByUsernameAndPassword(String username, String password) throws EntityNotFoundException;

    ResponseEntity<Void> activateUser(String token);
}
