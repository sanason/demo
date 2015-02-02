package com.shelleynason.expensetracker.service.security;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

/**
 * A filter that returns a 401 error status on non-SSL access.
 * (Shiro's {@link SslFilter} performs a redirect to the https port.)
 */
public class NonRedirectingSslFilter extends AccessControlFilter {
    
    @Override
    protected boolean isAccessAllowed(ServletRequest request,
            ServletResponse response, Object mappedValue) throws Exception {
        return request.isSecure();
    }
    
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        WebUtils.toHttp(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}
