package com.itech.model.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itech.utils.literal.ValidationMessageText;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Account data-transfer object to update existing Account.")
public class AccountUpdateDto {
    @JsonProperty("Amount")
    @Positive(message = ValidationMessageText.AMOUNT_MUST_BE_GREATER_THAN_ZERO_EXCEPTION_MESSAGE)
    @Schema(description = "Amount we need to update.")
    private double amount;
}
