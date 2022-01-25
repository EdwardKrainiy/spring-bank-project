package com.itech.model.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itech.utils.literal.JsonPropertyText;
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
  @JsonProperty(JsonPropertyText.ID)
  private Long id;

  @JsonProperty(JsonPropertyText.USERNAME)
  @Schema(description = "Unique username field of Account.")
  private String username;

  @JsonProperty(JsonPropertyText.AMOUNT)
  @Schema(description = "Amount field of Account.")
  private double amount;

  @JsonProperty(JsonPropertyText.CURRENCY)
  @Schema(description = "Currency field of Account.")
  private String currency;

  @JsonProperty(JsonPropertyText.IBAN)
  @Schema(description = "Unique Currency field of Account.")
  private String iban;
}
