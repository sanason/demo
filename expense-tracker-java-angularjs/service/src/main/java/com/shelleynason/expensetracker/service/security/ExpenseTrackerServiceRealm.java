package com.shelleynason.expensetracker.service.security;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.google.common.base.Preconditions;
import com.shelleynason.expensetracker.service.authentication.AuthenticationService;

public class ExpenseTrackerServiceRealm extends AuthorizingRealm {

    private final AuthenticationService authenticationService;
    
    public ExpenseTrackerServiceRealm(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        setAuthenticationTokenClass(TokenBasedAuthenticationToken.class);
    }
    
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        Preconditions.checkArgument(token instanceof TokenBasedAuthenticationToken);
        
        String authToken = ((TokenBasedAuthenticationToken) token).getToken();
        
        if (authToken == null) {
            throw new AuthenticationException("No token found.");
        }
        
        long userId = authenticationService.validateToken(authToken);

        return new SimpleAuthenticationInfo(userId, authToken, getName());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null) {
            throw new AuthorizationException("No principals found.");
        }

        Long userId = (Long) getAvailablePrincipal(principals);
        if (userId == null) {
            throw new AuthorizationException("Invalid userId");
        }
        
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addStringPermission(String.format("expense:*:%d", userId));
        return info;
    }
}
