package com.itech.utils.exception;

import org.springframework.http.ResponseEntity;

/**
 * ExpiredTokenException class.
 * @author Edvard Krainiy on ${date}
 * @version 1.0
 */
public class ExpiredTokenException extends Exception{
    public ExpiredTokenException(){
        super("This token is expired!");
        ResponseEntity.badRequest().body("This token is expired!");
    }
}
