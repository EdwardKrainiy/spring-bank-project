package com.itech.utils.exception.account;

/**
 * @author Edvard Krainiy on 12/18/2021
 */
public class AccountValidationException extends RuntimeException{
    public AccountValidationException(String message){
        super(message);
    }
}
