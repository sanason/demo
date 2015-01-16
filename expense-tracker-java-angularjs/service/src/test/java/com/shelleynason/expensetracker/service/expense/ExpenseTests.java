package com.shelleynason.expensetracker.service.expense;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Test;

public class ExpenseTests {

    private Validator validator;
    
    public ExpenseTests() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    public void testTooLongDescription() {
        String longString = "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";
        Expense expense = new Expense();
        expense.setDescription(longString);
        
        Set<ConstraintViolation<Expense>> violations = validator.validate(expense);
        assertEquals(violations.size(), 1);
        
        ConstraintViolation<Expense> violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().toString(), "description");
    }
    
    @Test
    public void testInvalidAmount() {
        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("22.333"));
        
        Set<ConstraintViolation<Expense>> violations = validator.validate(expense);
        assertEquals(violations.size(), 1);
        
        ConstraintViolation<Expense> violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().toString(), "amount");
    }
}
