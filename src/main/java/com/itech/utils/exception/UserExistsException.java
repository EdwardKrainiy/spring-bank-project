package com.itech.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * UserExistsException class.
 * @author Edvard Krainiy on 12/12/2021
 */

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UserExistsException extends RuntimeException{
    public UserExistsException(){
        super("This user already exists!");
    }
}
