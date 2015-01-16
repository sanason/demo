package com.shelleynason.expensetracker.service.user;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Test;

public class UserTests {

    private Validator validator;
    
    public UserTests() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    public void testEmptyUsername() {
        User user = new User();
        user.setUsername("");
        user.setPassword("abc123!!".toCharArray());
        
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(violations.size(), 1);
        
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().toString(), "username");
    }
    
    @Test
    public void testNullUsername() {
        User user = new User();
        user.setPassword("abc123!!".toCharArray());
        
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(violations.size(), 1);
        
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().toString(), "username");
    }
    
    @Test
    public void testNullPassword() {
        User user = new User();
        user.setUsername("bob");
        
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(violations.size(), 1);
        
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().toString(), "password");
    }
    
    @Test
    public void testShortPassword() {
        User user = new User();
        user.setUsername("SomeName");
        user.setPassword("short".toCharArray());
        
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(violations.size(), 1);
        
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().toString(), "password");
    }
}
