package com.shelleynason.expensetracker.service.expense;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import com.google.common.base.Preconditions;
import com.shelleynason.expensetracker.service.common.NotFoundException;
import com.shelleynason.expensetracker.service.user.User;
import com.shelleynason.expensetracker.service.user.UserService;

@Transactional
public class DefaultExpenseService implements ExpenseService {
    // before:2014-12-02+after:2014-12-02
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat timeFormat = new SimpleDateFormat("HH:mm");
    
    private final ExpenseRepository expenseRepository;
    private final UserService userService;
    private final Validator validator;
    
    public DefaultExpenseService(ExpenseRepository expenseRepository,
            UserService userService, Validator validator) {
        this.expenseRepository = expenseRepository;
        this.userService = userService;
        this.validator = validator;
    }

    @Override
    public List<Expense> listExpenses(long userId, Map<String, String> filterParams) {
        checkAuthorization("read", userId);
        
        if (filterParams.isEmpty()) {
            return expenseRepository.findByUserId(userId);
        } else {
            Specification<Expense> specification = ExpenseSpecifications.forUser(userId);
            specification = addFilterCriteria(specification, filterParams);
            return expenseRepository.findAll(specification);
        }
    }

    @Override
    public Expense addExpense(long userId, Expense expense) throws NotFoundException {
        checkAuthorization("create", userId);
        Preconditions.checkNotNull(expense);
        validateForAdd(userId, expense);
        return expenseRepository.save(expense);
    }

    @Override
    public Expense updateExpense(long userId, long id, Expense expense) throws NotFoundException {
        checkAuthorization("update", userId);
        Preconditions.checkNotNull(expense);
        validateForUpdate(userId, id, expense);
        return expenseRepository.save(expense);
    }

    @Override
    public void deleteExpense(long userId, long id) throws NotFoundException {
        checkAuthorization("delete", userId);
        validateForDelete(userId, id);
        expenseRepository.delete(id);
    }
    
    @Override
    public List<Map<String,Object>> computeAggregate(long userId,
            String ftn, String field, String grouping) {
        checkAuthorization("read", userId);
        return expenseRepository.computeAggregate(userId, ftn, field, grouping);
    }
    
    // All users are permitted all operations on expenses belonging to them.
    private void checkAuthorization(String action, long userId) {
        Subject currentUser = SecurityUtils.getSubject();
        
        // Operation is permitted if userId matches current user
        String permission = String.format("expense:%s:%d", action, userId);
        if (!currentUser.isPermitted(permission)) {
            throw new AuthorizationException("User is not permitted " + permission);
        }       
    }
    
    private Specification<Expense> addFilterCriteria(Specification<Expense> specification,
            Map<String, String> filterParams) {
        
        Specifications<Expense> specBuilder = Specifications.where(specification);
        for (String key : filterParams.keySet()) {
            switch(key) {
            case "date": {
                String[] dateArgs = filterParams.get(key).split(" ");
                for (String dateArg : dateArgs) {
                    if (dateArg.startsWith("before:")) {
                        try {
                            Date before = dateFormat.parse(dateArg.substring("before:".length()));
                            specBuilder = specBuilder.and(ExpenseSpecifications.dateBefore(before));
                        } catch (ParseException e) {
                            // Ignore
                        }
                    } else if (dateArg.startsWith("after:")) {
                        try {
                            Date after = dateFormat.parse(dateArg.substring("after:".length()));
                            specBuilder = specBuilder.and(ExpenseSpecifications.dateAfter(after));
                        } catch (ParseException e) {
                            // Ignore
                        }
                    }
                }
                break;
            }
            case "time": {
                String[] timeArgs = filterParams.get(key).split(" ");
                for (String timeArg : timeArgs) {
                    if (timeArg.startsWith("before:")) {
                        try {
                            Date before = timeFormat.parse(timeArg.substring("before:".length()));
                            specBuilder = specBuilder.and(ExpenseSpecifications.timeBefore(before));
                        } catch (ParseException e) {
                            // Ignore
                        }
                    } else if (timeArg.startsWith("after:")) {
                        try {
                            Date after = timeFormat.parse(timeArg.substring("after:".length()));
                            specBuilder = specBuilder.and(ExpenseSpecifications.timeAfter(after));
                        } catch (ParseException e) {
                            // Ignore
                        }
                    }
                }
                break;
            }
            case "description": {
                specBuilder = specBuilder.and(ExpenseSpecifications.descriptionContains(filterParams.get(key)));
                break;
            }
            case "amount": {
                String[] amountArgs = filterParams.get(key).split(" ");
                for (String amountArg : amountArgs) {
                    if (amountArg.startsWith("lessThan:")) {
                        try {
                            BigDecimal lessThan = new BigDecimal(amountArg.substring("lessThan:".length()));
                            specBuilder = specBuilder.and(ExpenseSpecifications.amountLessThan(lessThan));
                        } catch (NumberFormatException e) {
                            // Ignore
                        }
                    } else if (amountArg.startsWith("moreThan:")) {
                        try {
                            BigDecimal moreThan = new BigDecimal(amountArg.substring("moreThan:".length()));
                            specBuilder = specBuilder.and(ExpenseSpecifications.amountMoreThan(moreThan));
                        } catch (NumberFormatException e) {
                            // Ignore
                        }
                    }
                }
                break;                
            } 
            case "comment":
                specBuilder = specBuilder.and(ExpenseSpecifications.commentContains(filterParams.get(key)));
                break;
            default:
                break;
            }
        }
        return specBuilder;
    }

    private void validateForAdd(long userId, Expense expense) throws NotFoundException {
        User user = userService.getUserById(userId);
        expense.setUser(user);

        Set<ConstraintViolation<Expense>> constraints = validator.validate(expense);
        if (!constraints.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(constraints));
        }
    }

    private void validateForUpdate(long userId, long id, Expense expense) throws NotFoundException {
        Expense saved = expenseRepository.findOne(id);
        if (saved == null) {
            throw new NotFoundException("No expense with ID " + id);
        }
        expense.setId(id);

        if (saved.getUser().getId() != userId) {
            throw new NotFoundException("No expense with ID " + id + " for user with ID " + userId);
        }
        expense.setUser(saved.getUser());

        Set<ConstraintViolation<Expense>> constraints = validator.validate(expense);
        if (!constraints.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(constraints));
        }
    }

    private void validateForDelete(long userId, long id) throws NotFoundException {
        Expense expense = expenseRepository.findOne(id);
        if (expense != null) {
            if (expense.getUser().getId() != userId) {
                throw new NotFoundException("No expense with ID " + id + " for user with ID " + userId);
            }
        }
    }
}
