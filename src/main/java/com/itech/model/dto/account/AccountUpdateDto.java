package com.itech.model.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itech.model.enumeration.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Account data-transfer object to update Account.
 *
 * @author Edvard Krainiy on 12/21/2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountUpdateDto {
    @JsonProperty("Currency")
    private Currency currency; //TODO: AccountUpdateDTO and AccountCreateDTO are the same. You can move common fields to abstract class and extend them

    @JsonProperty("Amount")
    private double amount;
}
