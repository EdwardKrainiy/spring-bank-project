package com.itech.model.dto.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itech.model.dto.operation.OperationDto;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Transaction data-transfer object to manipulate with DB.")
public class TransactionDto {
    @JsonProperty("Id")
    @Schema(description = "Unique Id field of Transaction.")
    private Long id;

    @JsonProperty("UserId")
    @Schema(description = "UserId field of Transaction.")
    private Long userId;

    @JsonProperty("IssuedAt")
    @Schema(description = "IssuedAt field of Transaction. Contain time and date of creation.")
    private Date issuedAt;

    @JsonProperty("Status")
    @Schema(description = "Status field of Transaction. Can be IN_PROGRESS, CREATED, REJECTED, EXPIRED.")
    private String status;

    @JsonProperty("Operations")
    @Schema(description = "Set of operation in Transaction.")
    private Set<OperationDto> operations;
}
