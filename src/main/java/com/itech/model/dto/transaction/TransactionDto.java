package com.itech.model.dto.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itech.model.dto.operation.OperationDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

/**
 * Transaction data-transfer object to manipulate with DB.
 *
 * @author Edvard Krainiy on 12/23/2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    @JsonProperty("Id")
    private Long id;

    @JsonProperty("UserId")
    private Long userId;

    @JsonProperty("IssuedAt")
    private Date issuedAt;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("Operations")
    private Set<OperationDto> operations;
}
