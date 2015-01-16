package com.shelleynason.expensetracker.app;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handle requests for the app.
 */
@Controller
public class HomeController {
    private final String apiEndpoint;

    /**
     * Construct with dependencies.
     * @param apiEndpoint URL of the REST API
     */
    public HomeController(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    /**
     * Load the app on requests for '/'.
     * @return a view containing the app and a model defining the url for the corresponding REST API
     */
    @RequestMapping(value = "/")
    public String home(Model model) {
        model.addAttribute("apiEndpoint", apiEndpoint);
        return "home";
    }

}
