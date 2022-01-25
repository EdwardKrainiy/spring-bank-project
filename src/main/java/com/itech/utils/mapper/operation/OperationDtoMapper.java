package com.itech.utils.mapper.operation;

import com.itech.model.dto.operation.OperationDto;
import com.itech.model.entity.Operation;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * OperationDtoMapper interface, which contains method to transform Operation to OperationDto.
 *
 * @author Edvard Krainiy on 12/23/2021
 */
@Mapper(componentModel = "spring")
public interface OperationDtoMapper {
  /**
   * toDto method. Converts Operation object to OperationDto.
   *
   * @param operation Operation object, which we want to convert,
   * @return OperationDto Obtained OperationDto object.
   */
  @Mappings({
    @Mapping(target = "accountId", source = "operation.account.id"),
    @Mapping(target = "transactionId", source = "operation.transaction.id")
  })
  OperationDto toDto(Operation operation);

  @Mappings({
    @Mapping(target = "accountId", source = "operation.account.id"),
    @Mapping(target = "transactionId", source = "operation.transaction.id")
  })
  Set<OperationDto> toDtos(Set<Operation> operations);
}
