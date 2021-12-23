package com.itech.utils.exception.transaction;

/**
 * @author Edvard Krainiy on 12/23/2021
 */
public class TransactionNotFoundException extends RuntimeException{
    public TransactionNotFoundException(){
        super("Transaction not found!");
    }
}
