package com.itech.utils.exception.account;

/**
 * @author Edvard Krainiy on 12/18/2021
 */
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException() {
        super("Account not found!");
    }
}
