package com.shelleynason.expensetracker.service.user;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;

import javax.validation.Validator;

import org.apache.shiro.authc.credential.PasswordService;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import com.shelleynason.expensetracker.service.common.NotFoundException;
import com.shelleynason.expensetracker.service.common.UniquenessConstraintViolationException;

public class UserServiceTests {

    @Test(expected=NotFoundException.class)
    public void testGetUserByIdNotFound() throws NotFoundException {
        UserRepository mockRepository = Mockito.mock(UserRepository.class);
        Mockito.when(mockRepository.findOne(2L)).thenReturn(null);
        
        Validator mockValidator = Mockito.mock(Validator.class);
        
        PasswordService mockPasswordService = Mockito.mock(PasswordService.class);
        
        UserService userService = new UserService(mockRepository, mockPasswordService, mockValidator);
        userService.getUserById(2);
    }
    
    @Test(expected=NotFoundException.class)
    public void testGetUserByUsernameNotFound() throws NotFoundException {
        UserRepository mockRepository = Mockito.mock(UserRepository.class);
        Mockito.when(mockRepository.findByUsername("bob")).thenReturn(new ArrayList<User>());
        
        Validator mockValidator = Mockito.mock(Validator.class);
        
        PasswordService mockPasswordService = Mockito.mock(PasswordService.class);
        
        UserService userService = new UserService(mockRepository, mockPasswordService, mockValidator);
        userService.getUserByUsername("bob");
    }
    
    @Test
    public void testAddUserNonUnique() throws UniquenessConstraintViolationException {
        UserRepository mockRepository = Mockito.mock(UserRepository.class);
        Mockito.when(mockRepository.save(Mockito.any(User.class))).thenThrow(new DataIntegrityViolationException("invalid"));
        
        Validator mockValidator = Mockito.mock(Validator.class);
        
        char[] password = "password".toCharArray();
        char[] nulledPassword = "ssssssss".toCharArray();
        
        PasswordService mockPasswordService = Mockito.mock(PasswordService.class);
        Mockito.when(mockPasswordService.encryptPassword(password)).thenReturn("yyyyyyyyyy%");
        
        UserService userService = new UserService(mockRepository, mockPasswordService, mockValidator);
        
        User user = new User();
        user.setUsername("bob");
        user.setPassword(password);
        
        try {
            userService.addUser(user);
            fail("Expected a UniquenessConstraintViolationException.");
        } catch (UniquenessConstraintViolationException e) {
            assertArrayEquals(user.getPassword(), nulledPassword);
        }
    }
}
