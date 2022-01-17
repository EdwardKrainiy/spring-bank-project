package com.itech.model.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Schema(description = "User data-transfer object to sign in application.")
public class UserSignInDto {
    private static final String VALID_USERNAME_ADDRESS_REGEX = "^[a-zA-Z0-9._-]{3,}$";

    @JsonProperty("Username")
    @Pattern(regexp = VALID_USERNAME_ADDRESS_REGEX, message = "Username is not valid!")
    @NotBlank(message = "Username is empty!")
    @Schema(description = "Unique Username field of User.")
    private String username;

    @JsonProperty("Password")
    @NotBlank(message = "Password is empty!")
    @Size(min = 5, max = 20, message = "Incorrect password length! It must be from 5 to 20.")
    @Schema(description = "Password field of User.")
    private String password;
}
