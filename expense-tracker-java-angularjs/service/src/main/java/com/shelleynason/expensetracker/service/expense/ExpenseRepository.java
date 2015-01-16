package com.shelleynason.expensetracker.service.expense;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * A repository for {@link Expense} objects.
 * To be magically implemented by Spring Data.
 */
public interface ExpenseRepository extends CrudRepository<Expense, Long>, JpaSpecificationExecutor<Expense>, ExpenseRepositoryCustom {
    
    List<Expense> findByUserId(long userId);

}
