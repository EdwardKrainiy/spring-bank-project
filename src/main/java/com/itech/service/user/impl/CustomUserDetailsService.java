package com.itech.service.user.impl;

import com.itech.repository.UserRepository;
import com.itech.utils.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailsService class for authentication.
 * @autor Edvard Krainiy on ${date}
 * @version 1.0
 */

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * loadUserByUsername method. Returns us userDetails of the user, found by username.
     * @param username Username of the user, whose UserDetails we want to obtain.
     * @return UserDetails Returns UserDetails of found by username user.
     * @throws UserNotFoundException If user wasn't found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        UserDetails userDetails = null;

        try{
            com.itech.model.User user = userRepository.getUserByUsername(username).orElseThrow(()-> new UserNotFoundException(username));
            userDetails = User.withUsername(user.getUsername()).password(user.getPassword()).authorities(user.getRole().name()).build();
        }

        catch (UserNotFoundException exception){
            exception.printStackTrace();
        }

        return userDetails;
    }
}
