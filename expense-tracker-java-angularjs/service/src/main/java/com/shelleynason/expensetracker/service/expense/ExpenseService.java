package com.shelleynason.expensetracker.service.expense;

import java.util.List;
import java.util.Map;

import com.shelleynason.expensetracker.service.common.NotFoundException;

public interface ExpenseService {

    /**
     * List expenses for a user.
     * @param userId id of the user
     * @param filterParams fields to filter on
     * @return List of expenses.
     */
    List<Expense> listExpenses(long userId, Map<String, String> filterParams);
    
    /**
     * Add expense for a user.
     * @param userId User ID
     * @param expense Non-null expense
     * @return new expense with assigned ID
     * @throws NotFoundException If no user matching user ID
     */
    Expense addExpense(long userId, Expense expense) throws NotFoundException;
    
    /**
     * Modify an existing expense for a user.
     * @param userId User ID
     * @param id Expense ID
     * @param expense Non-null expense
     * @return updated expense
     * @throws NotFoundException if no expense matching id or if userId does not match saved userId 
     */
    Expense updateExpense(long userId, long id, Expense expense) throws NotFoundException;
    
    /**
     * Delete an existing expense for a user.
     * @param userId User ID
     * @param id Expense ID. Call will still succeed if there is no expense with this ID.
     * @throws NotFoundException if userId does not match saved userId 
     */
    void deleteExpense(long userId, long id) throws NotFoundException;
    
    List<Map<String,Object>> computeAggregate(long userId, String ftn, String field, String grouping);
}
