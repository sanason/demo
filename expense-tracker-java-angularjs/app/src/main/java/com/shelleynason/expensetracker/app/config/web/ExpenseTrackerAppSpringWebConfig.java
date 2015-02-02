package com.shelleynason.expensetracker.app.config.web;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.shelleynason.expensetracker.app.HomeController;

/**
 * Spring MVC configuration for the servlet that delivers the app.
 */
@Configuration
@EnableWebMvc
@PropertySource("classpath:app.properties")
public class ExpenseTrackerAppSpringWebConfig extends WebMvcConfigurerAdapter {

    // TODO Log environment on startup
    
    @Inject
    Environment env;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
    
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer){
        configurer.enable();
    }

    @Bean
    public ViewResolver configureViewResolver() {
        InternalResourceViewResolver viewResolve = new InternalResourceViewResolver();
        viewResolve.setPrefix("/WEB-INF/views/");
        viewResolve.setSuffix(".jsp");

        return viewResolve;
    }

    @Bean
    public HomeController homeController() {
        return new HomeController(env.getRequiredProperty("apiEndpoint"));
    }
}
