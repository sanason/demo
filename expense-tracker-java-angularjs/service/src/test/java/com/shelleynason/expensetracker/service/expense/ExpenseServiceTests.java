package com.shelleynason.expensetracker.service.expense;

import javax.validation.Validator;

import org.junit.Test;
import org.mockito.Mockito;

import com.shelleynason.expensetracker.service.common.NotFoundException;
import com.shelleynason.expensetracker.service.user.User;
import com.shelleynason.expensetracker.service.user.UserService;

// TODO Write authorization tests
// Can ExpenseService be decoupled from Shiro security?
public class ExpenseServiceTests {
    
    @Test(expected=NotFoundException.class)
    public void testAddExpenseInvalidUserId() throws NotFoundException {
        ExpenseRepository mockRepository = Mockito.mock(ExpenseRepository.class);
        
        UserService mockUserService = Mockito.mock(UserService.class);
        Mockito.when(mockUserService.getUserById(2)).thenThrow(new NotFoundException("No such user."));
        
        Validator mockValidator = Mockito.mock(Validator.class);
        
        ExpenseService service = new DefaultExpenseService(mockRepository, mockUserService, mockValidator);
       
        Expense expense = new Expense();
        service.addExpense(2, expense);
    }
    
    @Test(expected=NotFoundException.class)
    public void testUpdateExpenseInvalidExpenseId() throws NotFoundException {
        ExpenseRepository mockRepository = Mockito.mock(ExpenseRepository.class);
        Mockito.when(mockRepository.findOne(2L)).thenReturn(null);
        
        UserService mockUserService = Mockito.mock(UserService.class);
        Mockito.when(mockUserService.getUserById(1)).thenReturn(new User());
        
        Validator mockValidator = Mockito.mock(Validator.class);
        
        ExpenseService service = new DefaultExpenseService(mockRepository, mockUserService, mockValidator);
        
        Expense expense = new Expense();
        service.updateExpense(1, 2, expense);
    }
    
    @Test(expected=NotFoundException.class)
    public void testUpdateExpenseInvalidUserId() throws NotFoundException {
        User user = new User();
        user.setId(1);
        
        Expense saved = new Expense();
        saved.setUser(user);
        
        ExpenseRepository mockRepository = Mockito.mock(ExpenseRepository.class);
        Mockito.when(mockRepository.findOne(1L)).thenReturn(saved);
        
        UserService mockUserService = Mockito.mock(UserService.class);
        
        Validator mockValidator = Mockito.mock(Validator.class);
        
        ExpenseService service = new DefaultExpenseService(mockRepository, mockUserService, mockValidator);
        
        Expense expense = new Expense();
        service.updateExpense(2, 1, expense);
    }
    
    @Test(expected=NotFoundException.class)
    public void testDeleteExpenseInvalidUserId() throws NotFoundException {
        User user = new User();
        user.setId(1);
        
        Expense saved = new Expense();
        saved.setUser(user);
        
        ExpenseRepository mockRepository = Mockito.mock(ExpenseRepository.class);
        Mockito.when(mockRepository.findOne(1L)).thenReturn(saved);
        
        UserService mockUserService = Mockito.mock(UserService.class);
        
        Validator mockValidator = Mockito.mock(Validator.class);
        
        ExpenseService service = new DefaultExpenseService(mockRepository, mockUserService, mockValidator);
      
        service.deleteExpense(2, 1);
    }
  
}
