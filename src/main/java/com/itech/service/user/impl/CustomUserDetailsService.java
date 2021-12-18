package com.itech.service.user.impl;

import com.itech.repository.UserRepository;
import com.itech.utils.exception.user.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailsService class for authentication.
 *
 * @author Edvard Krainiy on 12/10/2021
 */

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * loadUserByUsername method. Returns us userDetails of the user, found by username.
     *
     * @param username Username of the user, whose UserDetails we want to obtain.
     * @return UserDetails Returns UserDetails of found by username user.
     * @throws UserNotFoundException If user wasn't found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) {

        com.itech.model.User user = userRepository.getUserByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        UserDetails userDetails = User.withUsername(user.getUsername()).password(user.getPassword()).authorities(user.getRole().name()).build();

        return userDetails;
    }
}
