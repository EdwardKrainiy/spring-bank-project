package com.itech.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * IncorrectPasswordException class.
 * @author Edvard Krainiy on 12/12/2021
 */

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class IncorrectPasswordException extends RuntimeException{
    public IncorrectPasswordException(String username){
        super(String.format("Incorrect password for username = %s!", username));
    }
}
