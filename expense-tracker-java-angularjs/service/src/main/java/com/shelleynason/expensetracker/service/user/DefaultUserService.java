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
public class DefaultUserService implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultUserService.class);

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final Validator validator;
    
    public DefaultUserService(UserRepository userRepository, PasswordService passwordService, Validator validator) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.validator = validator;
    }
    
    @Override
    public List<User> listUsers() {
        return Lists.newArrayList(userRepository.findAll());
    }
    
    @Override
    public User getUserById(long userId) throws NotFoundException {
        User user = userRepository.findOne(userId);
        if (user == null) {
            throw new NotFoundException("No such user");
        }
        return user;
    }
    
    @Override
    public User getUserByUsername(String username) throws NotFoundException {
        List<User> users = userRepository.findByUsername(username);
        if (users.isEmpty()) {
            throw new NotFoundException("No such user");
        }
        return users.get(0);
    }

    @Override
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
