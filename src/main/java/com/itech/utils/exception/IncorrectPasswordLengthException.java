package com.itech.utils.exception;

import org.springframework.http.ResponseEntity;

/**
 * IncorrectPasswordLengthException class.
 * @author Edvard Krainiy on ${date}
 * @version 1.0
 */

public class IncorrectPasswordLengthException extends Exception{
    public IncorrectPasswordLengthException(){
        super("Incorrect password length! Length must be from 0 to 10 symbols.");
        ResponseEntity.badRequest().body("Incorrect password length! Length must be from 0 to 10 symbols.");
    }
}
