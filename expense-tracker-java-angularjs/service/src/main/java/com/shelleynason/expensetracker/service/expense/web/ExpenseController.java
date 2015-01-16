package com.shelleynason.expensetracker.service.expense.web;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shelleynason.expensetracker.service.common.NotFoundException;
import com.shelleynason.expensetracker.service.expense.Expense;
import com.shelleynason.expensetracker.service.expense.ExpenseService;

// TODO Add sorting on query parameters
// TODO Add pagination using link headers (see Github)

/**
 * REST endpoint for expenses.
 */
@RestController
@RequestMapping("/users/{userId}/expenses")
public class ExpenseController {
    
    private final ExpenseService expenseService;
    
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }
    
    @RequestMapping(method=RequestMethod.GET)
    public List<Expense> list(
            @PathVariable long userId, @RequestParam Map<String, String> filterParams) {      
        return expenseService.listExpenses(userId, filterParams);
    }
    
    @RequestMapping(method=RequestMethod.POST, consumes="application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Expense create(@PathVariable long userId, @RequestBody Expense expense) 
            throws NotFoundException {
        return expenseService.addExpense(userId, expense);
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.PUT, consumes="application/json")
    @ResponseStatus(HttpStatus.OK)
    public Expense update(@PathVariable long userId, @PathVariable long id, @RequestBody Expense expense)
            throws NotFoundException {
        return expenseService.updateExpense(userId, id, expense);
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long userId, @PathVariable long id) throws NotFoundException {
        expenseService.deleteExpense(userId, id);
    }
    
    // TODO Add filterParams as in list() and compute aggregate over filtered list
    /**
     * Compute an aggregate over a user's expenses
     * @param userId the user whose expenses will be aggregated
     * @param ftn the aggregation ftn (sum, avg, etc. - only sum is implemented)
     * @param field the field to aggregate over (only supports 'amount')
     * @param grouping the unit to aggregate by (week, day, description - only 'week' is implemented)
     * @return a list of data { key : groupingKey, ftn_field : aggregateValue }
     */
    @RequestMapping(value="/aggregates", method=RequestMethod.GET)
    public List<Map<String,Object>> computeAggregate(
            @PathVariable long userId, @RequestParam("ftn") String ftn,
            @RequestParam("field") String field,
            @RequestParam("grouping") String grouping) {
        return expenseService.computeAggregate(userId, ftn, field, grouping);
    }

}
