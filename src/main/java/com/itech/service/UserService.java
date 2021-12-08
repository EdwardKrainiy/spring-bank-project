package com.itech.service;

import com.itech.model.User;
import com.itech.model.dto.UserDto;

public interface UserService {
    String createUser(UserDto userDto);

    User findUserByUsername(String username);

    User findUserByUsernameAndPassword(String username, String password);
}
