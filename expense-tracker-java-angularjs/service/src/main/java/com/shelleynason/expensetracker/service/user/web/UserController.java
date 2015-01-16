package com.shelleynason.expensetracker.service.user.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shelleynason.expensetracker.service.common.NotFoundException;
import com.shelleynason.expensetracker.service.common.UniquenessConstraintViolationException;
import com.shelleynason.expensetracker.service.user.User;
import com.shelleynason.expensetracker.service.user.UserService;

/**
 * Handle REST requests about users.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @RequestMapping(method=RequestMethod.GET)
    public List<User> list() {      
        return userService.listUsers();
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public User get(@PathVariable String id) throws NotFoundException {
        if (isLong(id)) {
            Long userId = Long.parseLong(id);
            return userService.getUserById(userId);
        } else {
            return userService.getUserByUsername(id);
        }
    }
    
    @RequestMapping(method=RequestMethod.POST, consumes="application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) throws UniquenessConstraintViolationException {
        return userService.addUser(user);
    }
      
    private boolean isLong(String s) {
        try {
            Long.parseLong(s);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }
}
