package com.shelleynason.expensetracker.service.config.web;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.shelleynason.expensetracker.service.authentication.web.AuthenticationController;
import com.shelleynason.expensetracker.service.common.web.GlobalControllerExceptionHandler;
import com.shelleynason.expensetracker.service.config.ExpenseTrackerServiceSecurityConfig;
import com.shelleynason.expensetracker.service.config.ExpenseTrackerServiceSpringConfig;
import com.shelleynason.expensetracker.service.expense.web.ExpenseController;
import com.shelleynason.expensetracker.service.user.web.UserController;

/**
 * Spring MVC configuration for the API servlet.
 */
@Configuration
@EnableWebMvc
public class ExpenseTrackerServiceSpringWebConfig extends WebMvcConfigurerAdapter {

    @Inject
    ExpenseTrackerServiceSpringConfig serviceConfig;
    
    @Inject
    ExpenseTrackerServiceSecurityConfig securityConfig;
    
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.simpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        builder.modules(Arrays.<Module>asList(new Hibernate4Module()));
        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
    }
    
    @Bean
    public GlobalControllerExceptionHandler exceptionHandler() {
        return new GlobalControllerExceptionHandler();
    }
    
    @Bean
    public UserController userController() {
        return new UserController(serviceConfig.userService());
    }
    
    @Bean
    public ExpenseController expenseController() {
        return new ExpenseController(serviceConfig.expenseService());
    }
    
    @Bean
    public AuthenticationController authenticationController() {
        return new AuthenticationController(securityConfig.authenticationService());
    }
}
