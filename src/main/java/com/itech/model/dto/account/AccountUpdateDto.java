package com.itech.model.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itech.model.enumeration.Currency;
import com.itech.validator.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

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
    @NotBlank(message = "Currency is empty!")
    @Size(min = 3, max = 3, message = "Currency size must be 3 letters!")
    @EnumValue(enumClass = Currency.class, message = "Incorrect Currency!")
    private Currency currency;

    @JsonProperty("Amount")
    @Positive(message = "Amount must be greater than 0!")
    private double amount;
}
