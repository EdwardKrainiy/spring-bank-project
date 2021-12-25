package com.itech.utils.mapper.operation;

import com.itech.model.dto.operation.OperationDto;
import com.itech.model.entity.Operation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author Edvard Krainiy on 12/23/2021
 */
@Mapper(componentModel = "spring")
public interface OperationDtoMapper {
    @Mappings({
            @Mapping(source = "operation.account.id", target = "accountId"),
            @Mapping(source = "operation.transaction.id", target = "transactionId"),
    })
    OperationDto toDto(Operation operation);
}
