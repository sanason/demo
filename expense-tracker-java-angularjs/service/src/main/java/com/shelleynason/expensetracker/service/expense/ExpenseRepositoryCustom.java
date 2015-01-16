package com.shelleynason.expensetracker.service.expense;

import java.util.List;
import java.util.Map;

public interface ExpenseRepositoryCustom {

    List<Map<String, Object>> computeAggregate(long userId, String ftn, String field,
            String grouping);
}
