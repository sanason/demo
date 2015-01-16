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
        int sslPort = env.getProperty("security.sslPort",Integer.class);
        return new NonRedirectingSslFilter(sslPort);
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
