package com.itech.model.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * User data-transfer object to manipulate with DB.
 *
 * @author Edvard Krainiy on 12/8/2021
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSignInDto {
    private static final String VALID_USERNAME_ADDRESS_REGEX = "^[a-zA-Z0-9._-]{3,}$";

    @JsonProperty("Username")
    @Pattern(regexp = VALID_USERNAME_ADDRESS_REGEX, message = "Username is not valid!")
    @NotBlank(message = "Username is empty!")
    private String username;

    @JsonProperty("Password")
    @NotBlank(message = "Password is empty!")
    @Size(min = 5, max = 20, message = "Incorrect password length! It must be from 5 to 20.")
    private String password;
}
