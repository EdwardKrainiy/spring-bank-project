package com.itech.utils.exception;

/**
 * UserIdNotFoundException.
 * @author Edvard Krainiy on 12/13/2021
 */
public class UserIdNotFoundException extends RuntimeException{
    public UserIdNotFoundException(String message){
        super(message);
    }
}
