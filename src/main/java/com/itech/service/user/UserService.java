package com.itech.service.user;

import com.itech.model.entity.User;
import com.itech.model.dto.user.UserDto;
import org.springframework.http.ResponseEntity;

/**
 * UserService interface. Provides us different methods to work with User objects on Service layer.
 *
 * @author Edvard Krainiy on 12/7/2021
 */

public interface UserService {
    ResponseEntity<Void> createUser(UserDto userDto);

    User findUserByUsername(String username);

    User findUserByUsernameAndPassword(String username, String password);

    ResponseEntity<Void> activateUser(String token);
}
