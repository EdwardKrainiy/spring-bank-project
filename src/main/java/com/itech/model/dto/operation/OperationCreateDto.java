package com.itech.model.dto.operation;

import com.itech.model.enumeration.OperationType;
import lombok.*;

import javax.validation.constraints.NotNull;


/**
 * @author Edvard Krainiy on 12/23/2021
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperationCreateDto {
    @NotNull
    private String accountNumber;
    @NotNull
    private double amount;
    @NotNull
    private OperationType operationType;
}
