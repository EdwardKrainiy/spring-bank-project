package com.itech.utils.exception;

import org.springframework.http.ResponseEntity;

/**
 * IncorrectPasswordException class.
 * @author Edvard Krainiy on ${date}
 * @version 1.0
 */

public class IncorrectPasswordException extends Exception{
    public IncorrectPasswordException(String username){
        super("Incorrect password for user " + username + "!");
        ResponseEntity.badRequest().body("Incorrect password for user " + username + "!");
    }
}
