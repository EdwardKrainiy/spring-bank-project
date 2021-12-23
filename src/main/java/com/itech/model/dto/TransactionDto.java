package com.itech.model.dto;

import lombok.*;

import java.util.Date;

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
}
