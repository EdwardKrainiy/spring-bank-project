package com.itech.model.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpDto extends UserSignInDto{
    private static final String VALID_EMAIL_ADDRESS_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    @JsonProperty("Email")
    @Pattern(regexp = VALID_EMAIL_ADDRESS_REGEX, message = "Email is not valid!")
    @NotBlank(message = "Email is empty!")
    private String email;
}
