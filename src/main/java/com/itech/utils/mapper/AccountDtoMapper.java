package com.itech.utils.mapper;

import com.itech.model.dto.AccountCreateDto;
import com.itech.model.entity.Account;
import org.mapstruct.Mapper;

/**
 * @author Edvard Krainiy on 12/18/2021
 */
@Mapper(componentModel = "spring")
public interface AccountDtoMapper {
    Account toEntity(AccountCreateDto accountCreateDto);

    AccountCreateDto toDto(Account account);
}
