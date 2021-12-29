package com.itech.utils.mapper.transaction;

import com.itech.model.dto.transaction.TransactionDto;
import com.itech.model.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * TransactionDtoMapper interface, which contains method to transform Transaction to TransactionDto.
 *
 * @author Edvard Krainiy on 12/23/2021
 */

@Mapper(componentModel = "spring")
public interface TransactionDtoMapper {
    /**
     * toDto method. Converts Transaction object to TransactionDto.
     *
     * @param transaction Transaction object we need to convert.
     * @return TransactionDto Obtained TransactionDto entity.
     */
    @Mappings({
            @Mapping(source = "transaction.user.id", target = "userId")
    })
    TransactionDto toDto(Transaction transaction);
}
