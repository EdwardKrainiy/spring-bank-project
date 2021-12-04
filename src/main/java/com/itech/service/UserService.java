package com.itech.service;

import com.itech.dao.UserRepository;
import com.itech.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User findUserById(Long id){
        return userRepository.findById(id);
    }

    public List<User> findAllUsers(){
        return userRepository.findAll();
    }

    public Long createUser(User user){
        return userRepository.create(user);
    }

    public void updateUser(User account){
        userRepository.update(account);
    }

    public void deleteUser(User account){
        userRepository.delete(account);
    }
}
