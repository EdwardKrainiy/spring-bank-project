package com.itech.utils;

import com.itech.model.Role;
import com.itech.model.User;
import com.itech.model.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DtoMappingUtils {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDto UserToDto(User user){
        return new UserDto(user.getUsername(), user.getPassword(), user.getEmail());
    }

    public User DtoToUser(UserDto userDto){
        return new User(userDto.getUsername(), passwordEncoder.encode(userDto.getPassword()), userDto.getEmail(), Role.USER);
    }
}
