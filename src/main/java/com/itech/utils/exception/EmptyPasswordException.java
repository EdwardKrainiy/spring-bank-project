package com.itech.utils.exception;

import org.springframework.http.ResponseEntity;

/**
 * EmptyPasswordException class.
 * @author Edvard Krainiy on ${date}
 * @version 1.0
 */
public class EmptyPasswordException extends Exception{
    public EmptyPasswordException(){
        super("Missing password!");
        ResponseEntity.badRequest().body("Missing password!");
    }
}
