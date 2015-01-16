package com.shelleynason.expensetracker.service.common.web;

import javax.validation.ConstraintViolationException;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.shelleynason.expensetracker.service.common.NotFoundException;
import com.shelleynason.expensetracker.service.common.UniquenessConstraintViolationException;

/**
 * Map exceptions to HTTP responses.
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @ResponseStatus(value=HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody ValidationErrorInfo handleInvalidRequest(ConstraintViolationException ex) {
        logger.debug("Validation error", ex);
        return new ValidationErrorInfo("Validation Failed", ex.getConstraintViolations());
    }
    
    @ResponseStatus(value=HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(UniquenessConstraintViolationException.class)
    @ResponseBody ErrorInfo handleUniquenessViolation(UniquenessConstraintViolationException ex) {
        logger.debug("Uniquness violation", ex);
        return new ErrorInfo(ex.getMessage());
    }

    @ResponseStatus(value=HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody ErrorInfo handleNotFoundRequest(NotFoundException ex) {
        logger.debug("Not found", ex);
        return new ErrorInfo(ex.getMessage());
    }
    
    @ResponseStatus(value=HttpStatus.FORBIDDEN)
    @ExceptionHandler(AuthorizationException.class)
    @ResponseBody ErrorInfo forbiddenRequest(AuthorizationException ex) {
        logger.info("Unauthorized action attempted.", ex);
        return new ErrorInfo("Unauthorized request");
    }
    
    @ResponseStatus(value=HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody ErrorInfo unauthorizedRequest(AuthenticationException ex) {
        logger.info("Authentication failure", ex);
        return new ErrorInfo("Invalid credentials");
    }
}
