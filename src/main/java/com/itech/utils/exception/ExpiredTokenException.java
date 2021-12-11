package com.itech.utils.exception;

import org.springframework.http.ResponseEntity;

/**
 * @author Edvard Krainiy
 */
public class ExpiredTokenException extends Exception{
    public ExpiredTokenException(){
        super("This token is expired!");
        ResponseEntity.badRequest().body("This token is expired!");
    }
}
