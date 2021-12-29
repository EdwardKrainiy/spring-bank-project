package com.itech.model.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Account data-transfer object to manipulate with DB.
 *
 * @author Edvard Krainiy on 12/18/2021
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    @JsonProperty("Id")
    private Long id;

    @JsonProperty("Username")
    private String username;

    @JsonProperty("Amount")
    private double amount;

    @JsonProperty("Currency")
    private String currency;

    @JsonProperty("IBAN")
    private String iban;
}
