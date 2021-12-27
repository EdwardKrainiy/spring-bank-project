package com.itech.utils.mapper.operation;

import com.itech.model.dto.operation.OperationDto;
import com.itech.model.entity.Operation;
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
            @Mapping(source = "operation.account.id", target = "accountId"),
            @Mapping(source = "operation.transaction.id", target = "transactionId"),
    })
    OperationDto toDto(Operation operation);
}
