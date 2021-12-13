package com.itech.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Edvard Krainiy on 12/13/2021
 */

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UserValidationException extends RuntimeException{
    public UserValidationException(String message){
        super(message);
    }
}
