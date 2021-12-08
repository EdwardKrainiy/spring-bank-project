package com.itech.utils;

import com.itech.model.Role;
import com.itech.model.User;
import com.itech.model.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DtoMappingUtils {

    public UserDto UserToDto(User user){
        return new UserDto(user.getUsername(), user.getPassword(), user.getEmail());
    }

    public User DtoToUser(UserDto userDto){
        return new User(userDto.getUsername(), userDto.getPassword(), userDto.getEmail(), Role.USER);
    }
}
