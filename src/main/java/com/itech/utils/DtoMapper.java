package com.itech.utils;

import com.itech.model.Account;
import com.itech.model.User;
import com.itech.model.dto.AccountDto;
import com.itech.model.dto.UserDto;
import org.mapstruct.Mapper;

/**
 * DtoMapper interface, which contains methods to transform User and UserDto both ways.
 *
 * @author Edvard Krainiy on 12/9/2021
 */

@Mapper(componentModel = "spring")
public interface DtoMapper {

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
