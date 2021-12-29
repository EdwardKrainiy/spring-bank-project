package com.itech.utils.exception;

/**
 * ValidationException class.
 *
 * @author Edvard Krainiy on 12/27/2021
 */
public class ValidationException extends RuntimeException{
    public ValidationException(String message){
        super(message);
    }
}
