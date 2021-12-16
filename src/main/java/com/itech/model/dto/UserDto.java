package com.itech.model.dto;

import lombok.*;

/**
 * User data-transfer object to manipulate with DB.
 * @author Edvard Krainiy on 12/8/2021
 */

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String username;
    private String password;
    private String email;
}
