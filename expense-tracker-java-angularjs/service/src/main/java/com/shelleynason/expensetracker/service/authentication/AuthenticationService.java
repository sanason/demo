package com.shelleynason.expensetracker.service.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import java.util.Arrays;
import java.util.Date;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.credential.PasswordService;

import com.shelleynason.expensetracker.service.common.NotFoundException;
import com.shelleynason.expensetracker.service.user.User;
import com.shelleynason.expensetracker.service.user.UserService;

public class AuthenticationService {
    private static final int ONE_MINUTE_IN_MILLIS = 60*1000;

    private final UserService userService;
    private final PasswordService passwordService;
    private final int expirationInMilliseconds;
    private final byte[] signingKey;

    public AuthenticationService(UserService userService, PasswordService passwordService,
            int expirationInMinutes, byte[] signingKey) {
        this.userService = userService;
        this.passwordService = passwordService;
        this.expirationInMilliseconds = expirationInMinutes*ONE_MINUTE_IN_MILLIS;
        this.signingKey = signingKey;
    }

    /**
     * Authenticate a username/password pair.
     * If successful, return an authentication token and information about the relevant user.
     * Throws exceptions on failure.
     * @param user User to authenticate. Must contain a username and password.
     * @return non-null {@link AuthenticatedUser}
     */
    public AuthenticatedUser authenticate(User user) {
        try {
            if (user.getUsername() == null || user.getPassword() == null) {
                throw new AuthenticationException("Username and password are required."); 
            }

            User storedUser;
            try {
                storedUser = userService.getUserByUsername(user.getUsername());
            } catch (NotFoundException e) {
                throw new AuthenticationException("No user " + user.getUsername());
            }

            if (!passwordService.passwordsMatch(user.getPassword(), storedUser.getHashedPassword())) {
                throw new AuthenticationException("Incorrect password.");
            }

            return createAuthenticatedUser(storedUser);
        } finally {
            Arrays.fill(user.getPassword(), 's');
        }
    }

    /**
     * Validate an authentication token.
     * Checks the token was signed by this service and has not expired.
     * If valid, returns the id of the user to whom the token was granted.
     * @param tokenString token to validate
     * @return if token is valid, returns the userId stored in the token
     */
    public long validateToken(String tokenString) {
        try {
            Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(tokenString).getBody();
            return Long.valueOf(claims.getSubject());
        } catch (ExpiredJwtException e) {
            throw new ExpiredCredentialsException("Expired token", e);
        } catch (MalformedJwtException | SignatureException | UnsupportedJwtException e) {
            throw new IncorrectCredentialsException("Bad token", e);
        }
    }

    private AuthenticatedUser createAuthenticatedUser(User user) {
        Long expiration = new Date().getTime() + expirationInMilliseconds;
        String signedToken = Jwts.builder()
                .setSubject(Long.toString(user.getId()))
                .setExpiration(new Date(expiration))
                .signWith(SignatureAlgorithm.HS256, signingKey)
                .compact();
            
        return new AuthenticatedUser(signedToken, expiration, user);
    }
}
