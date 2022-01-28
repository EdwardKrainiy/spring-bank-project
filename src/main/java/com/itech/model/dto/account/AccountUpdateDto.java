package com.itech.model.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itech.utils.literal.DtoJsonProperty;
import com.itech.utils.literal.ValidationExceptionMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Account data-transfer object to update existing Account.
 *
 * @author Edvard Krainiy on 12/18/2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Account data-transfer object to update existing Account.")
public class AccountUpdateDto {
  @JsonProperty(DtoJsonProperty.AMOUNT)
  @Positive(message = ValidationExceptionMessage.AMOUNT_MUST_BE_GREATER_THAN_ZERO_EXCEPTION_MESSAGE)
  @Schema(description = "Amount we need to update.")
  private double amount;
}
