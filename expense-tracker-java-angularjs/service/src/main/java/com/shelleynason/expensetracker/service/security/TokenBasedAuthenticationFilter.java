package com.shelleynason.expensetracker.service.security;

import java.util.Locale;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenBasedAuthenticationFilter extends AuthenticatingFilter {
    private static final Logger logger = LoggerFactory.getLogger(TokenBasedAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    
    private final String authzScheme;
    
    public TokenBasedAuthenticationFilter(String authzScheme) {
        this.authzScheme = authzScheme;
    }
    
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        // Authentication is always required
        return false;
    }
    
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (executeLogin(request, response)) {
            return true;
        } else {
            WebUtils.toHttp(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        String authorizationHeader = httpRequest.getHeader(AUTHORIZATION_HEADER);
        
        if (authorizationHeader == null) {
            return new TokenBasedAuthenticationToken("");
        }
        
        String[] authTokens = authorizationHeader.split(" ");
        if (authTokens == null || authTokens.length != 2) {
            return new TokenBasedAuthenticationToken("");
        }
        
        if (!authTokens[0].toLowerCase(Locale.ENGLISH).equals(authzScheme)) {
            return new TokenBasedAuthenticationToken("");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Attempting to execute login with headers [" + authorizationHeader + "]");
        }

        return new TokenBasedAuthenticationToken(authTokens[1]);
    }
}
