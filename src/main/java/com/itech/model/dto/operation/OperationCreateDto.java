package com.itech.model.dto.operation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itech.model.enumeration.OperationType;
import com.itech.utils.literal.DtoJsonProperty;
import com.itech.utils.literal.ValidationExceptionMessage;
import com.itech.validator.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Operation data-transfer object to create Operation.
 *
 * @author Edvard Krainiy on 12/23/2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Operation data-transfer object to create new Operation.")
public class OperationCreateDto {

  @NotBlank(message = ValidationExceptionMessage.ACCOUNT_NUMBER_IS_EMPTY_EXCEPTION_MESSAGE)
  @JsonProperty(DtoJsonProperty.ACCOUNT_NUMBER)
  @Schema(description = "Account Number field of Account, which we want to use in our operation.")
  private String accountNumber;

  @JsonProperty(DtoJsonProperty.AMOUNT)
  @Positive(message = ValidationExceptionMessage.AMOUNT_MUST_BE_GREATER_THAN_ZERO_EXCEPTION_MESSAGE)
  @Schema(description = "Amount field of Operation.")
  private double amount;

  @JsonProperty(DtoJsonProperty.OPERATION_TYPE)
  @EnumValue(
      enumClass = OperationType.class,
      message = ValidationExceptionMessage.INCORRECT_OPERATION_TYPE_EXCEPTION_MESSAGE)
  @Schema(description = "Operation Type field of Operation. Can be CREDIT or DEBIT.")
  private String operationType;
}
