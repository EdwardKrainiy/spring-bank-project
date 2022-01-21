package com.itech.model.dto.operation;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Operation data-transfer object to manipulate with DB.
 *
 * @author Edvard Krainiy on 12/23/2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Operation data-transfer object to manipulate with DB.")
public class OperationDto {
    @JsonProperty("Id")
    @Schema(description = "Unique Id field of Operation.")
    private Long id;

    @JsonProperty("AccountId")
    @Schema(description = "AccountId field of Operation.")
    private Long accountId;

    @JsonProperty("TransactionId")
    @Schema(description = "TransactionId field of Operation.")
    private Long transactionId;

    @JsonProperty("Amount")
    @Schema(description = "Amount field of Operation.")
    private Double amount;

    @JsonProperty("OperationType")
    @Schema(description = "OperationType field of Operation. Can be CREDIT or DEBIT.")
    private String operationType;
}
