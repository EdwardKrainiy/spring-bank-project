package com.itech.model.dto.operation;

import lombok.*;

/**
 * @author Edvard Krainiy on 12/23/2021
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperationDto {
    private Long id;
    private Long accountId;
    private Long transactionId;
    private Double amount;
    private String operationType;
}
