package com.itech.model.dto.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itech.model.dto.operation.OperationCreateDto;
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
    @JsonProperty("Operations")
    @NotEmpty(message = "Operations is empty!")
    @Size(min = 2, message = "Minimal size of operations is 2!")
    @IsOperationCorrect
    @Schema(description = "Set of operations of Transaction to create new Transaction.")
    private Set<@Valid OperationCreateDto> operations;
}
