package com.itech.model.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountUpdateDto {
    @JsonProperty("Amount")
    @Positive(message = "Amount must be greater than 0!")
    private double amount;
}
