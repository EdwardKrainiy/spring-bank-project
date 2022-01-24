package com.itech.model.dto.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itech.model.dto.operation.OperationCreateDto;
import com.itech.utils.literal.JsonPropertyText;
import com.itech.utils.literal.ValidationMessageText;
import com.itech.validator.annotation.IsOperationCorrect;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * Transaction data-transfer object to create Transaction.
 *
 * @author Edvard Krainiy on 12/23/2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Transaction data-transfer object to create new Transaction.")
public class TransactionCreateDto {
    @JsonProperty(JsonPropertyText.OPERATIONS)
    @NotEmpty(message = ValidationMessageText.EMPTY_OPERATIONS_EXCEPTION_MESSAGE)
    @Size(min = 2, message = ValidationMessageText.MINIMAL_OPERATIONS_SIZE_EXCEPTION_MESSAGE)
    @IsOperationCorrect(message = ValidationMessageText.INCORRECT_STRUCTURE_OF_OPERATIONS_EXCEPTION_MESSAGE)
    @Schema(description = "Set of operations of Transaction to create new Transaction.")
    private Set<@Valid OperationCreateDto> operations;
}
