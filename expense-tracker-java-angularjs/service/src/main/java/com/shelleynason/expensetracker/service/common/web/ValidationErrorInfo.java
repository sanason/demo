package com.shelleynason.expensetracker.service.common.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.validation.ConstraintViolation;

/**
 * DTO to accompany HTTP responses with error status 422 Unprocessable Entity.
 * Provides a detailed list of invalid fields on the entity.
 */
public class ValidationErrorInfo extends ErrorInfo {
    public final List<ViolationInfo> errors = new ArrayList<>();

    public ValidationErrorInfo(String message, Collection<ConstraintViolation<?>> violations) {
        super(message);
        
        for (ConstraintViolation<?> violation : violations) {
            errors.add(new ViolationInfo(violation.getRootBeanClass().getSimpleName(), 
                    violation.getPropertyPath().toString(), violation.getMessage()));
        }
    }
    
    public static class ViolationInfo {
        public final String resource;
        public final String field;
        public final String message;
        
        public ViolationInfo(String resource, String field, String message) {
            this.resource = resource;
            this.field = field;
            this.message = message;
        }
    }
}