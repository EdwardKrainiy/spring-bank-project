package com.itech.service.user.impl;

import com.itech.repository.UserRepository;
import com.itech.utils.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
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

    private final UserRepository userRepository;
    @Value("${exception.user.not.found}")
    private String userNotFoundExceptionText;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * loadUserByUsername method. Returns us userDetails of the user, found by username.
     *
     * @param username Username of the user, whose UserDetails we want to obtain.
     * @return UserDetails Returns UserDetails of found by username user.
     * @throws EntityNotFoundException If user wasn't found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        com.itech.model.entity.User user = userRepository.findUserByUsername(username).orElseThrow(() -> new EntityNotFoundException(userNotFoundExceptionText));
        return User.withUsername(user.getUsername()).password(user.getPassword()).authorities(user.getRole().name()).build();
    }
}
