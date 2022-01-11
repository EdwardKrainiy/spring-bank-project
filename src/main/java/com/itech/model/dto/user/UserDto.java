package com.itech.model.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Size;

/**
 * User data-transfer object to manipulate with DB.
 *
 * @author Edvard Krainiy on 12/8/2021
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @JsonProperty("Username")
    private String username;

    @JsonProperty("Password")
    @Size(min = 5, max = 20, message = "Incorrect password length! It must be from 5 to 20.")
    private String password;

    @JsonProperty("Email")
    private String email; //TODO: add email validation
}
