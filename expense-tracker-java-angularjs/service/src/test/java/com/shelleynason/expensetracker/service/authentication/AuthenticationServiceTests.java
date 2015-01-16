package com.shelleynason.expensetracker.service.authentication;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.codec.Hex;
import org.junit.Test;
import org.mockito.Mockito;

import com.shelleynason.expensetracker.service.common.NotFoundException;
import com.shelleynason.expensetracker.service.user.User;
import com.shelleynason.expensetracker.service.user.UserService;

public class AuthenticationServiceTests {

    @Test
    public void testAuthenticateAndValidate() throws NotFoundException {
        char[] password = "password".toCharArray();
        
        UserService mockUserService = Mockito.mock(UserService.class);
        User savedUser = new User();
        savedUser.setId(10);
        savedUser.setUsername("bob");
        savedUser.setHashedPassword("hashedPassword");
        Mockito.when(mockUserService.getUserByUsername("bob")).thenReturn(savedUser);
        
        PasswordService mockPasswordService = Mockito.mock(PasswordService.class);
        Mockito.when(mockPasswordService.passwordsMatch(password, "hashedPassword")).thenReturn(true);
        
        String signingKey = "19bf";
        AuthenticationService authService = new AuthenticationService(mockUserService, mockPasswordService, 5, Hex.decode(signingKey));
        
        User authenticatingUser = new User();
        authenticatingUser.setUsername("bob");
        authenticatingUser.setPassword(password);
        AuthenticatedUser authUser = authService.authenticate(authenticatingUser);
        
        long userId = authService.validateToken(authUser.token);
        assertEquals(10, userId);
    }
    
    @Test
    public void testAuthenticateWithInvalidPassword() throws NotFoundException {
        UserService mockUserService = Mockito.mock(UserService.class);
        User savedUser = new User();
        savedUser.setUsername("bob");
        savedUser.setHashedPassword("hashedPassword");   
        Mockito.when(mockUserService.getUserByUsername("bob")).thenReturn(savedUser);
        
        char[] wrongPassword = "wrongPassword".toCharArray();
        char[] nulledWrongPassword = "sssssssssssss".toCharArray();
        PasswordService mockPasswordService = Mockito.mock(PasswordService.class);
        Mockito.when(mockPasswordService.passwordsMatch(wrongPassword, "hashedPassword")).thenReturn(false);
        
        String signingKey = "19bf";
        AuthenticationService authService = new AuthenticationService(mockUserService, mockPasswordService, 5, Hex.decode(signingKey));
             
        User user = new User();
        user.setUsername("bob");
        user.setPassword(wrongPassword);
        
        try {
            authService.authenticate(user);
            fail("Expected an AuthenticationException");
        } catch (AuthenticationException e) {
            assertArrayEquals(user.getPassword(), nulledWrongPassword);
        }
        
    }
}
