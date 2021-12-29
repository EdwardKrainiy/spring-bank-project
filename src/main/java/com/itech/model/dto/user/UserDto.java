package com.itech.model.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

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
    private String password;

    @JsonProperty("Email")
    private String email;
}
