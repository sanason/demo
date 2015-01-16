package com.shelleynason.expensetracker.service.expense;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class ExpenseRepositoryImpl implements ExpenseRepositoryCustom {

    private final JdbcTemplate jdbc;
    
    public ExpenseRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // TODO Make this as generic as the method parameters suggest
    //      (support different functions, fields, groupings)
    //      See commented code - I think it is the right approach, particularly
    //      if the aggregates should also support filtering
    @Override
    public List<Map<String, Object>> computeAggregate(long userId,
            String ftn, String field, String grouping) {


        String query = "SELECT sum(amount) as total, dateadd(DAY, 1-DAYOFWEEK(date), date) as weekStart";
        query += " FROM Expenses WHERE User_Id = ?";
        query += " GROUP BY weekStart";
        query += " ORDER BY weekStart desc";

        return jdbc.query(query, new Object[]{userId},
                new RowMapper<Map<String, Object>>() {

            @Override
            public Map<String, Object> mapRow(ResultSet rs, int rowNum)
                    throws SQLException {
                Map<String,Object> agg = new HashMap<>();

                agg.put("key", rs.getDate("weekStart").toString());
                agg.put("sum_amount", rs.getBigDecimal("total").toString());
                return agg;
            }

        });
    }
    
//    @PersistenceContext
//    EntityManager em;
//    
//    private Expression buildFunctionExpression(String ftn, String field, CriteriaBuilder builder, Root<Expense> root) {
//        // TODO Returned expression should depend on ftn and field
//        return builder.sum(root.<BigDecimal>get("amount"));
//    }
//        
//    private Expression buildGroupingExpression(String grouping, CriteriaBuilder builder, Root<Expense> root) {
//        // TODO Returned expression should depend on gropuing
//        return builder.function("dateadd", Timestamp.class,
//                // Why does Hibernate write DAY as a parameter?
//                builder.literal("DAY"),
//                builder.diff(1, builder.function("dayofweek", Integer.class, root.<Timestamp>get("date"))),
//                root.<Timestamp>get("date"));
//    }
//    
//    @Override
//    public List<Map<String, Object>> computeAggregate(long userId, String ftn, String field, String grouping) {
//        
//        CriteriaBuilder builder = em.getCriteriaBuilder();
//        CriteriaQuery<Tuple> query = builder.createTupleQuery();
//        Root<Expense> root = query.from(Expense.class);
//        
//        query.where(builder.equal(root.<Long>get("user"), userId));
//        
//        Expression functionExpression = buildFunctionExpression(ftn, field, builder, root);
//        Expression groupingSelection = buildGroupingExpression(grouping, builder, root);
//        query.select(builder.tuple(groupingSelection, functionExpression));
//                
//        query.groupBy(groupingSelection);
//               
//        List<Map<String,Object>> retVal = new ArrayList<>();
//        List<Tuple> results = em.createQuery(query).getResultList();
//        for (Tuple tuple : results) {
//            Map<String,Object> aggregate = new HashMap<>();
//            aggregate.put("key", tuple.get(0));
//            aggregate.put(ftn + "_" + field, tuple.get(1));
//            
//            retVal.add(aggregate);
//        }
//        
//        return retVal;
//    }
    
}
