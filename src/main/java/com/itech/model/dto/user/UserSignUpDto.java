package com.itech.model.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itech.utils.literal.DtoJsonProperty;
import com.itech.utils.literal.RegexPattern;
import com.itech.utils.literal.ValidationExceptionMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * User data-transfer object to create new User.
 *
 * @author Edvard Krainiy on 12/8/2021
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User data-transfer object to sign up application.")
public class UserSignUpDto extends UserSignInDto {

  @JsonProperty(DtoJsonProperty.EMAIL)
  @Pattern(
      regexp = RegexPattern.VALID_EMAIL_ADDRESS_REGEX,
      message = ValidationExceptionMessage.EMAIL_IS_NOT_VALID_EXCEPTION_MESSAGE_TEXT)
  @NotBlank(message = ValidationExceptionMessage.EMAIL_IS_EMPTY_MESSAGE_TEXT)
  @Schema(description = "Unique Email field of User.")
  private String email;
}
