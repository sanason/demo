package com.shelleynason.expensetracker.service.expense;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

/**
 * Spring Data JPA specifications for queries involving {@link Expense} objects.
 */
public class ExpenseSpecifications {

    public static Specification<Expense> forUser(final long userId) {
        return new Specification<Expense>() {

            @Override
            public Predicate toPredicate(Root<Expense> root,
                    CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.<Long>get("user"), userId);
            }
            
        };
      }
    
    public static Specification<Expense> descriptionContains(final String description) {
        return new Specification<Expense>() {

            @Override
            public Predicate toPredicate(Root<Expense> root,
                    CriteriaQuery<?> query, CriteriaBuilder cb) {
                // TODO Escape % and _ in description
                String searchString = "%" + description.toLowerCase() + "%";
                return cb.like(cb.lower(root.<String>get("description")), searchString);
            }
        };
    }

    public static Specification<Expense> commentContains(final String comment) {
        return new Specification<Expense>() {

            @Override
            public Predicate toPredicate(Root<Expense> root,
                    CriteriaQuery<?> query, CriteriaBuilder cb) {
                // TODO Escape % and _ in comment
                String searchString = "%" + comment.toLowerCase() + "%";
                return cb.like(cb.lower(root.<String>get("comment")), searchString);
            }
        };
    }

    public static Specification<Expense> dateBefore(final Date date) {
        return new Specification<Expense>() {

            @Override
            public Predicate toPredicate(Root<Expense> root,
                    CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.lessThanOrEqualTo(root.<Date>get("date"), date);
            }
        };
    }
    
    public static Specification<Expense> dateAfter(final Date date) {
        return new Specification<Expense>() {

            @Override
            public Predicate toPredicate(Root<Expense> root,
                    CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.greaterThanOrEqualTo(root.<Date>get("date"), date);
            }
        };
    }

    public static Specification<Expense> timeBefore(final Date time) {
        return new Specification<Expense>() {

            @Override
            public Predicate toPredicate(Root<Expense> root,
                    CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.lessThanOrEqualTo(root.<Date>get("time"), time);
            }
        };
    }

    public static Specification<Expense> timeAfter(final Date time) {
        return new Specification<Expense>() {

            @Override
            public Predicate toPredicate(Root<Expense> root,
                    CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.greaterThanOrEqualTo(root.<Date>get("time"), time);
            }
        };
    }

    public static Specification<Expense> amountLessThan(final BigDecimal lessThan) {
        return new Specification<Expense>() {

            @Override
            public Predicate toPredicate(Root<Expense> root,
                    CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.lessThanOrEqualTo(root.<BigDecimal>get("amount"), lessThan);
            }
        };
    }

    public static Specification<Expense> amountMoreThan(final BigDecimal moreThan) {
        return new Specification<Expense>() {

            @Override
            public Predicate toPredicate(Root<Expense> root,
                    CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.greaterThanOrEqualTo(root.<BigDecimal>get("amount"), moreThan);
            }
        };
    }
}
