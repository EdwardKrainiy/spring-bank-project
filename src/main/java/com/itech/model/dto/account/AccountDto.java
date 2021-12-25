package com.itech.model.dto.account;

import lombok.*;

/**
 * @author Edvard Krainiy on 12/18/2021
 */

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private Long id;
    private String username;
    private double amount;
    private String currency;
    private String iban;
}
