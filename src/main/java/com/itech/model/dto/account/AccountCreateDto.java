package com.itech.model.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itech.model.enumeration.Currency;
import com.itech.validator.EnumValue;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

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
    @NotBlank(message = "Currency is empty!")
    @Size(min = 3, max = 3, message = "Currency size must be 3 letters!")
    @EnumValue(enumClass = Currency.class, message = "Incorrect Currency!")
    private String currency;

    @JsonProperty("Amount")
    @Positive(message = "Amount must be greater than 0!")
    private double amount;
}
