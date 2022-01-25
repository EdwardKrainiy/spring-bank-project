package com.itech.utils.mapper.account;

import com.itech.model.dto.account.AccountDto;
import com.itech.model.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * AccountDtoMapper interface, which contains method to transform Account to AccountDto.
 *
 * @author Edvard Krainiy on 12/21/2021
 */
@Mapper(componentModel = "spring")
public interface AccountDtoMapper {
  /**
   * toDto method. Converts Account object to AccountDto.
   *
   * @param account Account object we need to convert.
   * @return AccountDto Obtained AccountDto entity.
   */
  @Mappings({
    @Mapping(source = "account.user.username", target = "username"),
    @Mapping(source = "account.currency", target = "currency"),
    @Mapping(source = "account.accountNumber", target = "iban")
  })
  AccountDto toDto(Account account);
}
