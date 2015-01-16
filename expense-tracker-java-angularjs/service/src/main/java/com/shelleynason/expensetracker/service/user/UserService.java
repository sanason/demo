package com.shelleynason.expensetracker.service.user;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.apache.shiro.authc.credential.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.shelleynason.expensetracker.service.common.NotFoundException;
import com.shelleynason.expensetracker.service.common.UniquenessConstraintViolationException;

/**
 * Service layer for {@link User} objects.
 */
@Transactional
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final Validator validator;
    
    public UserService(UserRepository userRepository, PasswordService passwordService, Validator validator) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.validator = validator;
    }
    
    /**
     * List all users.
     * @return list of users
     */
    public List<User> listUsers() {
        return Lists.newArrayList(userRepository.findAll());
    }
    
    /**
     * Retrieve a user given a user ID.
     * @param userId
     * @return user with given ID
     * @throws NotFoundException If no such user
     */
    public User getUserById(long userId) throws NotFoundException {
        User user = userRepository.findOne(userId);
        if (user == null) {
            throw new NotFoundException("No such user");
        }
        return user;
    }
    
    /**
     * Retrieve a user given a username.
     * @param username
     * @return user with given username
     * @throws NotFoundException If no such user
     */
    public User getUserByUsername(String username) throws NotFoundException {
        List<User> users = userRepository.findByUsername(username);
        if (users.isEmpty()) {
            throw new NotFoundException("No such user");
        }
        return users.get(0);
    }

    /**
     * Add a new user.
     * @param user Non-null user.
     * @return new user with assigned ID
     * @throws UniquenessConstraintViolationException If username already exists
     */
    public User addUser(User user) throws UniquenessConstraintViolationException {
        Preconditions.checkNotNull(user);

        try {
            logger.info("Creating user with username {}", user.getUsername());

            validateForAdd(user);

            String hashedPassword = passwordService.encryptPassword(user.getPassword());
            user.setHashedPassword(hashedPassword);

            try {
                return userRepository.save(user);
            } catch (DataIntegrityViolationException e) {
                throw new UniquenessConstraintViolationException("Username already exists");
            }
        } finally {
            Arrays.fill(user.getPassword(), 's');
        }
    }

    private void validateForAdd(User user) {
        Set<ConstraintViolation<User>> constraints = validator.validate(user);
        if (!constraints.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(constraints));
        }
    }
}
