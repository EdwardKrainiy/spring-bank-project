package com.itech.model.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itech.utils.literal.JsonPropertyText;
import com.itech.utils.literal.RegexPattern;
import com.itech.utils.literal.ValidationMessageText;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User data-transfer object to sign up application.")
public class UserSignUpDto extends UserSignInDto {

    @JsonProperty(JsonPropertyText.EMAIL)
    @Pattern(regexp = RegexPattern.VALID_EMAIL_ADDRESS_REGEX, message = ValidationMessageText.EMAIL_IS_NOT_VALID_EXCEPTION_MESSAGE_TEXT)
    @NotBlank(message = ValidationMessageText.EMAIL_IS_EMPTY_MESSAGE_TEXT)
    @Schema(description = "Unique Email field of User.")
    private String email;
}
