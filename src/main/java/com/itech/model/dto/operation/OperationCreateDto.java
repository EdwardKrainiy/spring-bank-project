package com.itech.model.dto.operation;

import com.itech.model.enumeration.OperationType;
import lombok.*;

import javax.validation.constraints.Min;
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

    @NotNull(message = "Account number is empty!")
    private String accountNumber;

    @Min(value = 1, message = "Amount is empty!")
    private double amount;

    @NotNull(message = "Operation Type number is empty!")
    private OperationType operationType;
}
