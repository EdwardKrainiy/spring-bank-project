package com.itech.model.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itech.model.enumeration.Currency;
import com.itech.validator.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Account data-transfer object to create Account.
 *
 * @author Edvard Krainiy on 12/18/2021
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Account data-transfer object to create new Account.")
public class AccountCreateDto extends AccountUpdateDto {
    @JsonProperty("Currency")
    @NotNull(message = "Currency is empty!")
    @EnumValue(enumClass = Currency.class, message = "Incorrect Currency!")
    @Schema(description = "Currency field to create unique IBAN.")
    private Currency currency;
}
