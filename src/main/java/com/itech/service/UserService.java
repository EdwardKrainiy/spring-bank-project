package com.itech.service;

import com.itech.model.User;

public interface UserService {
    String createUser(String username, String password, String email);

    User findUserByUsername(String username);

    User findUserByUsernameAndPassword(String username, String password);
}
