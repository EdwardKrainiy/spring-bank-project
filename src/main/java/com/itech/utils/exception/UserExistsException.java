package com.itech.utils.exception;

import org.springframework.http.ResponseEntity;

/**
 * UserExistsException class.
 * @author Edvard Krainiy on ${date}
 * @version 1.0
 */

public class UserExistsException extends Exception{
    public UserExistsException(){
        super("This user already exists!");
        ResponseEntity.badRequest().body("This user already exists!");
    }
}
