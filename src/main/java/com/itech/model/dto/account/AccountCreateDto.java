package com.itech.model.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Account data-transfer object to create Account.
 *
 * @author Edvard Krainiy on 12/18/2021
 */

@Data //TODO: data already includes getter and setter
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateDto {
    @JsonProperty("Currency")
    private String currency; //TODO: use enum here like in AccountUpdateDTO.

    @JsonProperty("Amount")
    private double amount;
}
