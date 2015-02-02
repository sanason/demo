package com.shelleynason.expensetracker.service.config;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.shiro.codec.Hex;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

import com.shelleynason.expensetracker.service.authentication.AuthenticationService;
import com.shelleynason.expensetracker.service.security.CorsFilter;
import com.shelleynason.expensetracker.service.security.ExpenseTrackerServiceRealm;
import com.shelleynason.expensetracker.service.security.NonRedirectingSslFilter;
import com.shelleynason.expensetracker.service.security.TokenBasedAuthenticationFilter;

/**
 * Configure Shiro security.
 */
@Configuration
@PropertySources({@PropertySource("classpath:service.properties"), @PropertySource("classpath:secret.properties")})
public class ExpenseTrackerServiceSecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseTrackerServiceSecurityConfig.class);
    
    @Inject
    ExpenseTrackerServiceSpringConfig serviceConfig;
    
    @Inject
    Environment env;

    @Bean
    public ShiroFilterFactoryBean shiroFilter() {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager());
        
        Map<String, String> filterChainDefinitions = new LinkedHashMap<>();
        filterChainDefinitions.put("/users/*/expenses/**", "noSessionCreation, nonRedirectingSslFilter, tokenAuthenticationFilter");
        filterChainDefinitions.put("/**", "noSessionCreation, nonRedirectingSslFilter");
        shiroFilter.setFilterChainDefinitionMap(filterChainDefinitions);
        
        return shiroFilter;
    }
    
    @Bean
    public TokenBasedAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenBasedAuthenticationFilter(env.getProperty("security.authzScheme"));
    }
    
    @Bean
    public WebSecurityManager securityManager() {
        return new DefaultWebSecurityManager(realm());
    }
    
    @Bean
    public Realm realm() {
        return new ExpenseTrackerServiceRealm(authenticationService());
    }
    
    @Bean
    public NonRedirectingSslFilter nonRedirectingSslFilter() {
        return new NonRedirectingSslFilter();
    }
    
    @Bean
    public CorsFilter corsFilter() {
        // By default, all origins are allowed to make requests.
        String allowedOrigins = env.getProperty("cors.allowedOrigins", "*");
        
        // By default, following methods are supported: GET, POST, HEAD and OPTIONS.
        String allowedHttpMethods = env.getProperty("cors.allowedHttpMethods", "GET,POST,HEAD,OPTIONS");
        
        // By default, following headers are supported:
        // Origin,Accept,X-Requested-With, Content-Type,
        // Access-Control-Request-Method, and Access-Control-Request-Headers.
        String allowedHttpHeaders = env.getProperty("cors.allowedHttpHeaders",
                "Origin,Accept,X-Requested-With,Content-Type," +
                "Access-Control-Request-Method,Access-Control-Request-Headers");
        
        // By default, none of the headers are exposed in response.
        String exposedHeaders = env.getProperty("cors.exposedHeaders", "");
        
        // By default, support credentials is turned on.
        boolean supportsCredentials = env.getProperty("cors.supportsCredentials", Boolean.class, true);
        
        // By default, time duration to cache pre-flight response is 30 mins.
        long preflightMaxAge = env.getProperty("cors.preflightMaxAge", Long.class, 1800L);
        
        // By default, request is decorated with CORS attributes.
        boolean decorateRequest = env.getProperty("cors.decorateRequest", Boolean.class, true);
        
        return new CorsFilter(allowedOrigins, allowedHttpMethods,
                allowedHttpHeaders, exposedHeaders, supportsCredentials, preflightMaxAge, decorateRequest);
    }
    
    @Bean
    public AuthenticationService authenticationService() {
        int tokenExpirationInMinutes = env.getProperty("security.tokenExpirationInMinutes", Integer.class);
        
        String signingKey = env.getProperty("security.signingKey");
        byte[] decodedKey = Hex.decode(signingKey);
        
        if (decodedKey.length < 256) {
            logger.warn("Signing key shorter than recommended 256 bits.");
        }
        
        return new AuthenticationService(serviceConfig.userService(), serviceConfig.passwordService(),
                tokenExpirationInMinutes, decodedKey);
    }
    
}
