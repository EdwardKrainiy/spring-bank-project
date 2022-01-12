package com.itech.utils.mapper.user;

import com.itech.model.dto.user.UserSignUpDto;
import com.itech.model.entity.User;
import org.mapstruct.Mapper;

/**
 * DtoMapper interface, which contains methods to transform User and UserDtoSignUp both ways.
 *
 * @author Edvard Krainiy on 01/11/2022
 */

@Mapper(componentModel = "spring")
public interface UserSignUpDtoMapper {

    /**
     * UserToUserDto method. Converts User object to UserDto.
     *
     * @param user User object we need to convert.
     * @return UserDto Obtained UserDto.
     */
    UserSignUpDto toDto(User user);

    /**
     * DtoUserToUser method. Converts UserDto to User object.
     *
     * @param userSignInDto UserDto we need to convert.
     * @return User Obtained User object.
     */
    User toEntity(UserSignUpDto userSignUpDto);


}