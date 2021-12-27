package com.itech.model.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Account data-transfer object to create Account.
 *
 * @author Edvard Krainiy on 12/18/2021
 */

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateDto {
    @JsonProperty("Currency")
    private String currency;

    @JsonProperty("Amount")
    private double amount;
}
