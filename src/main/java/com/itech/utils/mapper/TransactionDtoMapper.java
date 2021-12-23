package com.itech.utils.mapper;

import com.itech.model.dto.TransactionDto;
import com.itech.model.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author Edvard Krainiy on 12/23/2021
 */

@Mapper(componentModel = "spring")
public interface TransactionDtoMapper {
    /**
     * toDto method. Converts Account object to AccountCreateDto.
     *
     * @param transaction Account object we need to convert.
     * @return TransactionDto Obtained TransactionDto entity.
     */
    @Mappings({
            @Mapping(source = "transaction.user.id", target = "userId")
    })
    TransactionDto toDto(Transaction transaction);
}
