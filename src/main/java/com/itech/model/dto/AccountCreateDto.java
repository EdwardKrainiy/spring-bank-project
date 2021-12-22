package com.itech.model.dto;

import lombok.*;

/**
 * Account data-transfer object to manipulate with DB.
 *
 * @author Edvard Krainiy on 12/18/2021
 */

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateDto {
    private String currency;
    private double amount;
}
