package com.shelleynason.expensetracker.service.common;

/**
 * Exception thrown when expected object is not found.
 */
public class NotFoundException extends Exception {

    public NotFoundException(String message) {
        super(message);
    }

}
