package com.shelleynason.expensetracker.service.user;

import java.util.List;

import com.shelleynason.expensetracker.service.common.NotFoundException;
import com.shelleynason.expensetracker.service.common.UniquenessConstraintViolationException;

public interface UserService {
    
    /**
     * List all users.
     * @return list of users
     */
    List<User> listUsers();
    
    /**
     * Retrieve a user given a user ID.
     * @param userId
     * @return user with given ID
     * @throws NotFoundException If no such user
     */
    User getUserById(long userId) throws NotFoundException;
    
    /**
     * Retrieve a user given a username.
     * @param username
     * @return user with given username
     * @throws NotFoundException If no such user
     */
    User getUserByUsername(String username) throws NotFoundException;
    
    /**
     * Add a new user.
     * @param user Non-null user.
     * @return new user with assigned ID
     * @throws UniquenessConstraintViolationException If username already exists
     */
    User addUser(User user) throws UniquenessConstraintViolationException;

}
