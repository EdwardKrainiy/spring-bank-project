package com.itech.service.user;

import com.itech.model.User;
import com.itech.model.dto.UserDto;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<?> createUser(UserDto userDto);

    User findUserByUsername(String username);

    User findUserByUsernameAndPassword(String username, String password);

    ResponseEntity<?> activateUser(String token);
}
