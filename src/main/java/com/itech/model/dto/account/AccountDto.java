package com.itech.model.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Account data-transfer object to manipulate with DB.")
public class AccountDto {
    @Schema(description = "Unique Id field of Account.")
    @JsonProperty("Id")
    private Long id;

    @JsonProperty("Username")
    @Schema(description = "Unique username field of Account.")
    private String username;

    @JsonProperty("Amount")
    @Schema(description = "Amount field of Account.")
    private double amount;

    @JsonProperty("Currency")
    @Schema(description = "Currency field of Account.")
    private String currency;

    @JsonProperty("IBAN")
    @Schema(description = "Unique Currency field of Account.")
    private String iban;
}
