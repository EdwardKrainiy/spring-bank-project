package com.itech.model.dto.transaction;

import com.itech.model.dto.operation.OperationDto;
import lombok.*;

import java.util.Date;
import java.util.Set;

/**
 * @author Edvard Krainiy on 12/23/2021
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Long id;
    private Long userId;
    private Date issuedAt;
    private String status;
    private Set<OperationDto> operations;
}
