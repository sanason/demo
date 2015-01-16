package com.shelleynason.expensetracker.service.common;

/**
 * Exception thrown on attempting to set a unique field to a non-unique value.
 * Needs to be a runtime exception to trigger transaction rollback.
 */
public class UniquenessConstraintViolationException extends RuntimeException {

    public UniquenessConstraintViolationException(String message) {
        super(message);
    }
}
