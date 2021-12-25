package com.itech.utils.mapper.user;

import com.itech.model.dto.user.UserDto;
import com.itech.model.entity.User;
import org.mapstruct.Mapper;

/**
 * DtoMapper interface, which contains methods to transform User and UserDto both ways.
 *
 * @author Edvard Krainiy on 12/18/2021
 */

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    /**
     * UserToUserDto method. Converts User object to UserDto.
     *
     * @param user User object we need to convert.
     * @return UserDto Obtained UserDto.
     */
    UserDto toDto(User user);

    /**
     * DtoUserToUser method. Converts UserDto to User object.
     *
     * @param userDto UserDto we need to convert.
     * @return User Obtained User object.
     */
    User toEntity(UserDto userDto);
}