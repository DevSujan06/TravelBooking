package com.travelbooking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        // Redirect to login page
        return "login"; // Refers to login.html in the templates directory
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        // Show the register page
        return "register"; // Refers to register.html in the templates directory
    }

    @GetMapping("/login")
    public String showLoginPage() {
        // Render the login.html file
        return "login";
    }

    @GetMapping("/flight-search")
    public String showFlightSearchPage() {
        // Render the flight search page
        return "flights"; // Refers to flights.html in the templates directory
    }
}
