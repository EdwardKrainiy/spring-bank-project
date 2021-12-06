package com.itech.service.impl;

import com.itech.model.User;
import com.itech.repository.UserRepository;
import com.itech.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public String createUser(String username, String password, String email) {
        if(userRepository.getUserByUsername(username) == null){
            userRepository.save(new User(username, encoder.encode(password), email));
            return "Successful sign-up!";
        }
        else return "User with this username already exists!";
    }

    @Override
    public User findUserByUsername(String username) {
       return userRepository.getUserByUsername(username);

    }

    @Override
    public User findUserByUsernameAndPassword(String username, String password) {
        User foundUser = userRepository.getUserByUsername(username);
        if(foundUser != null){
            if(foundUser.getPassword().equals(password)){
                return foundUser;
            }
        }
        return null;
    }
}
