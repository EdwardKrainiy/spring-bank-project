package com.itech.utils.mapper.transaction;

import com.itech.model.dto.transaction.TransactionDto;
import com.itech.model.entity.Transaction;
import com.itech.utils.mapper.operation.OperationDtoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * TransactionDtoMapper interface, which contains method to transform Transaction to TransactionDto.
 *
 * @author Edvard Krainiy on 12/23/2021
 */
@Mapper(componentModel = "spring", uses = OperationDtoMapper.class)
public interface TransactionDtoMapper {
  /**
   * toDto method. Converts Transaction object to TransactionDto.
   *
   * @param transaction Transaction object we need to convert.
   * @return TransactionDto Obtained TransactionDto entity.
   */
  @Mappings({
    @Mapping(source = "transaction.user.id", target = "userId"),
    @Mapping(source = "transaction.issuedAt", target = "issuedAt")
  })
  TransactionDto toDto(Transaction transaction);
}
