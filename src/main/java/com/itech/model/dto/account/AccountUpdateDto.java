package com.itech.model.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @Positive(message = "Amount must be greater than 0!")
    @Schema(description = "Amount we need to update.")
    private double amount;
}
