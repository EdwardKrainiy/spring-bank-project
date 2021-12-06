package com.itech.service.impl;

import org.springframework.security.core.userdetails.User;
import com.itech.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.itech.model.User user = userRepository.getUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        
        UserDetails userDetails = User.withUsername(user.getUsername()).password(user.getPassword()).roles(user.getRole().name()).build();

        return userDetails;
    }
}
