package com.itech.utils.exception;

/**
 * @author Edvard Krainiy on 12/18/2021
 */
public class EntityValidationException extends RuntimeException{
    public EntityValidationException(String message){
        super(message);
    }
}
