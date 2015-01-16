package com.shelleynason.expensetracker.service.security;

import org.apache.shiro.authc.AuthenticationToken;

@SuppressWarnings("serial")
public class TokenBasedAuthenticationToken implements AuthenticationToken {

    private final String token;
    
    public TokenBasedAuthenticationToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return getToken();
    }

    @Override
    public Object getCredentials() {
        return getToken();
    }

    public String getToken() {
        return token;
    }

}
