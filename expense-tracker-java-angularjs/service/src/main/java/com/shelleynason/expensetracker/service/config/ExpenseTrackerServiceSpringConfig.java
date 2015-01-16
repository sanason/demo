package com.shelleynason.expensetracker.service.config;

import javax.inject.Inject;
import javax.validation.Validator;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.shelleynason.expensetracker.service.expense.ExpenseRepository;
import com.shelleynason.expensetracker.service.expense.ExpenseService;
import com.shelleynason.expensetracker.service.user.UserRepository;
import com.shelleynason.expensetracker.service.user.UserService;

@Configuration
public class ExpenseTrackerServiceSpringConfig {
   
    @Inject
    UserRepository userRepository;

    @Inject
    ExpenseRepository expenseRepository;

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public UserService userService() {
        return new UserService(userRepository, passwordService(), validator());
    }

    @Bean
    public ExpenseService expenseService() {
        return new ExpenseService(expenseRepository, userService(), validator());
    }
    
    @Bean
    public PasswordService passwordService() {
        // SHA-256, 500K iterations, salted
        DefaultPasswordService passwordService = new DefaultPasswordService();        
        return passwordService;
    }
}
