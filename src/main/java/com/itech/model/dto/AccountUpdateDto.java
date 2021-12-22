package com.itech.model.dto;

import com.itech.model.Currency;
import lombok.*;

/**
 * @author Edvard Krainiy on 12/21/2021
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountUpdateDto {
    private Currency currency;
    private double amount;
}
