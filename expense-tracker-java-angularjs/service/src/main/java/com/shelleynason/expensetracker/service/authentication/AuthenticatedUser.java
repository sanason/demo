package com.shelleynason.expensetracker.service.authentication;

import com.shelleynason.expensetracker.service.user.User;

/**
 * DTO to carry the results of a successful authentication.
 */
public class AuthenticatedUser {
    /**
     * An authentication token signed by the API service.
     * Subsequent requests to the service should carry the header:
     * Authorization: token <token>
     */
    public String token;
    
    /**
     * When the authentication token expires.
     */
    public Long expiration;
    
    /**
     * Info about the user who has been authenticated.
     */
    public User user;

    public AuthenticatedUser(String token, Long expiration, User user) {
        this.token = token;
        this.expiration = expiration;
        this.user = user;
    }
}
