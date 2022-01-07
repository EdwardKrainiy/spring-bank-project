package com.itech.model.dto.operation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;


/**
 * Operation data-transfer object to create Operation.
 *
 * @author Edvard Krainiy on 12/23/2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationCreateDto {

    @JsonProperty("AccountNumber")
    @NotNull(message = "Account number is empty!")
    private String accountNumber;

    @JsonProperty("Amount")
    @Positive(message = "Amount must be greater than 0!")
    private double amount;

    @JsonProperty("OperationType")
    @NotNull(message = "Operation Type number is empty!")
    private String operationType;
}
