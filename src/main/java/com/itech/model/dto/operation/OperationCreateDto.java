package com.itech.model.dto.operation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itech.model.enumeration.OperationType;
import com.itech.validator.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Operation data-transfer object to create new Operation.")
public class OperationCreateDto {

    @JsonProperty("AccountNumber")
    @NotNull(message = "Account number is empty!")
    @Schema(description = "Account Number field of Account, which we want to use in our operation." )
    private String accountNumber;

    @JsonProperty("Amount")
    @Positive(message = "Amount must be greater than 0!")
    @Schema(description = "Amount field of Operation." )
    private double amount;

    @JsonProperty("OperationType")
    @NotNull(message = "Operation Type number is empty!")
    @EnumValue(enumClass = OperationType.class, message = "Incorrect OperationType!")
    @Schema(description = "Operation Type field of Operation. Can be CREDIT or DEBIT." )
    private OperationType operationType;
}
