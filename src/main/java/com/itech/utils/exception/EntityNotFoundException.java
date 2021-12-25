package com.itech.utils.exception;

/**
 * @author Edvard Krainiy on 12/18/2021
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
