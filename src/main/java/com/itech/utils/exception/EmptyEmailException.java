package com.itech.utils.exception;

import org.springframework.http.ResponseEntity;

/**
 * EmptyEmailException class.
 * @author Edvard Krainiy on ${date}
 * @version 1.0
 */
public class EmptyEmailException extends Exception{
    public EmptyEmailException(){
        super("Missing email!");
        ResponseEntity.badRequest().body("Missing email!");
    }
}
