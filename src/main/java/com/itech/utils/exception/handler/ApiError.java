package com.itech.utils.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Custom API Errors class.
 * @author Edvard Krainiy on 12/13/2021
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
    private String message;
    private String debugMessage;

    public ApiError(String message){
        this.message = message;
    }
}
