package com.shelleynason.expensetracker.service.common.web;

/**
 * DTO to accompany HTTP responses with error status codes.
 * Provides information about the cause of the error.
 */
public class ErrorInfo {
    public final String message;

    public ErrorInfo(String message) {
        this.message = message;
    }
}
