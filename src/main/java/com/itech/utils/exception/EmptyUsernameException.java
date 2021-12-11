package com.itech.utils.exception;

import org.springframework.http.ResponseEntity;

/**
 * EmptyUsernameException class.
 * @author Edvard Krainiy on ${date}
 * @version 1.0
 */
public class EmptyUsernameException extends Exception{
    public EmptyUsernameException(){
        super("Missing username!");
        ResponseEntity.badRequest().body("Missing username!");
    }
}
