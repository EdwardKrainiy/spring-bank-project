package com.itech.utils.mapper.user;

import com.itech.model.dto.user.UserSignInDto;
import com.itech.model.entity.User;
import org.mapstruct.Mapper;

/**
 * DtoMapper interface, which contains methods to transform User and UserDtoSignIn both ways.
 *
 * @author Edvard Krainiy on 12/18/2021
 */
@Mapper(componentModel = "spring")
public interface UserSignInDtoMapper {

  /**
   * UserToUserDto method. Converts User object to UserDto.
   *
   * @param user User object we need to convert.
   * @return UserDto Obtained UserDto.
   */
  UserSignInDto toDto(User user);
}
