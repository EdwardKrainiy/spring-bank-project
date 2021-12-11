package com.itech.utils.exception;

import org.springframework.http.ResponseEntity;

/**
 * InvalidEmailException class.
 * @author Edvard Krainiy on ${date}
 * @version 1.0
 */

public class InvalidEmailException extends Exception{
    public InvalidEmailException(){
        super("Invalid email!");
        ResponseEntity.badRequest().body("Invalid email!");
    }
}
