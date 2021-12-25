package com.itech.utils.mapper.account;

import com.itech.model.dto.account.AccountCreateDto;
import com.itech.model.entity.Account;
import org.mapstruct.Mapper;

/**
 * AccountCreateDtoMapper interface, which contains methods to transform Account and AccountCreateDto both ways.
 *
 * @author Edvard Krainiy on 12/18/2021
 */
@Mapper(componentModel = "spring")
public interface AccountCreateDtoMapper {
    /**
     * toEntity method. Converts AccountCreateDto object to Account.
     *
     * @param accountCreateDto AccountCreateDto object we need to convert.
     * @return Account Obtained Account entity.
     */
    Account toEntity(AccountCreateDto accountCreateDto);

    /**
     * toDto method. Converts Account object to AccountCreateDto.
     *
     * @param account Account object we need to convert.
     * @return AccountCreateDto Obtained AccountCreateDto entity.
     */
    AccountCreateDto toDto(Account account);
}
