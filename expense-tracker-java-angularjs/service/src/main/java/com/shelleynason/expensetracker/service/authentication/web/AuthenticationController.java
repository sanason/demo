package com.shelleynason.expensetracker.service.authentication.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.shelleynason.expensetracker.service.authentication.AuthenticatedUser;
import com.shelleynason.expensetracker.service.authentication.AuthenticationService;
import com.shelleynason.expensetracker.service.user.User;

/**
 * Handle authentication requests.
 */
@Controller
@RequestMapping("/authenticate")
public class AuthenticationController {
    
    private final AuthenticationService authenticationService;
    
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    /**
     * Authenticate a username/password pair.
     * If successful, return an authentication token and information about the relevant user.
     * If fails, return 401 Unauthorized (see {@link GlobalControllerExceptionHandler}).
     * @param user User to authenticate. Must contain a username and password.
     * @return
     */
    @RequestMapping(method=RequestMethod.POST, consumes="application/json", produces="application/json")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody AuthenticatedUser authenticate(@RequestBody User user) {
        return authenticationService.authenticate(user);
    }
}

