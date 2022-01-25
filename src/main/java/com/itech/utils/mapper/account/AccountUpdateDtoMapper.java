package com.itech.utils.mapper.account;

import com.itech.model.dto.account.AccountUpdateDto;
import com.itech.model.entity.Account;
import org.mapstruct.Mapper;

/**
 * AccountUpdateDtoMapper interface, which contains method to transform AccountUpdateDto to Account.
 *
 * @author Edvard Krainiy on 12/21/2021
 */
@Mapper(componentModel = "spring")
public interface AccountUpdateDtoMapper {
  /**
   * toEntity method. Converts AccountUpdateDto object to Account.
   *
   * @param accountUpdateDto AccountUpdateDto object we need to convert.
   * @return Account Obtained Account entity.
   */
  Account toEntity(AccountUpdateDto accountUpdateDto);
}
