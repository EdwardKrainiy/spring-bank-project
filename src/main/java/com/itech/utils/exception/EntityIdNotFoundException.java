package com.itech.utils.exception;

/**
 * UserIdNotFoundException.
 *
 * @author Edvard Krainiy on 12/13/2021
 */
public class EntityIdNotFoundException extends RuntimeException {
    public EntityIdNotFoundException(String message) {
        super(message);
    }
}
