package com.itech.utils;

import com.itech.model.User;
import com.itech.model.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

/**
 * DtoMapper interface, which contains methods to transform User and UserDto both ways.
 * @author Edvard Krainiy on 12/9/2021
 */

@Component
@Mapper
public interface DtoMapper {

    /**
     * UserToUserDto method. Converts User object to UserDto.
     * @param user User object we need to convert.
     * @return UserDto Obtained UserDto.
     */
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "email", target = "email")
    public UserDto toDto(User user);

    /**
     * DtoUserToUser method. Converts UserDto to User object.
     * @param userDto UserDto we need to convert.
     * @return User Obtained User object.
     */
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "email", target = "email")
    public User toEntity(UserDto userDto);
}
