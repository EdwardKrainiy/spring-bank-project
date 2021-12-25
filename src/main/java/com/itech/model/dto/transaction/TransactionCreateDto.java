package com.itech.model.dto.transaction;

import com.itech.model.dto.operation.OperationCreateDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.*;

import java.util.Set;

/**
 * @author Edvard Krainiy on 12/23/2021
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCreateDto {
    @NotEmpty
    @Size(min = 2)
    private Set<OperationCreateDto> operations;
}
