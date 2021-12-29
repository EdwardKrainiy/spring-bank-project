package com.itech.model.dto.operation;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class OperationDto {
    @JsonProperty("Id")
    private Long id;

    @JsonProperty("AccountId")
    private Long accountId;

    @JsonProperty("TransactionId")
    private Long transactionId;

    @JsonProperty("Amount")
    private Double amount;

    @JsonProperty("OperationType")
    private String operationType;
}
