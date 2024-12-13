package com.travelbooking.controller;

import com.travelbooking.model.User;
import com.travelbooking.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Register a new user.
     */
    @PostMapping(value = "/register", consumes = {"application/json", "application/x-www-form-urlencoded"})
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<User> registerUser(
            @RequestBody(required = false) User requestBodyUser,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password) {

        logger.info("Register request received with username: {}, email: {}", username, email);

        User userToSave = requestBodyUser != null ? requestBodyUser : new User();
        if (requestBodyUser == null) {
            userToSave.setUsername(username);
            userToSave.setEmail(email);
            userToSave.setPassword(password);
        }

        if (userToSave.getUsername() == null || userToSave.getEmail() == null || userToSave.getPassword() == null) {
            logger.error("Invalid registration request: Missing fields");
            return Mono.error(new RuntimeException("Missing required fields: username, email, or password"));
        }

        return userService.registerUser(userToSave)
                .doOnSuccess(registeredUser -> logger.info("User registered successfully: {}", registeredUser.getUsername()))
                .onErrorResume(error -> {
                    logger.error("Registration failed for user: {}", userToSave, error);
                    return Mono.error(new RuntimeException("Registration failed: " + error.getMessage()));
                });
    }

    /**
     * Authenticate a user (login).
     */
    @PostMapping(value = "/login", consumes = {"application/json", "application/x-www-form-urlencoded"})
    public Mono<String> loginUser(
            @RequestBody(required = false) User requestBodyUser,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password) {

        logger.info("Login request received for email: {}", email);

        User userToAuthenticate = requestBodyUser != null ? requestBodyUser : new User();
        if (requestBodyUser == null) {
            userToAuthenticate.setEmail(email);
            userToAuthenticate.setPassword(password);
        }

        if (userToAuthenticate.getEmail() == null || userToAuthenticate.getPassword() == null) {
            logger.error("Invalid login request: Missing email or password");
            return Mono.error(new RuntimeException("Missing email or password"));
        }

        return userService.findByEmail(userToAuthenticate.getEmail())
                .flatMap(foundUser -> {
                    if (foundUser.getPassword().equals(userToAuthenticate.getPassword())) {
                        logger.info("Login successful for user: {}", foundUser.getUsername());
                        return Mono.just("/flights");
                    } else {
                        logger.error("Invalid credentials for email: {}", userToAuthenticate.getEmail());
                        return Mono.error(new RuntimeException("Invalid credentials"));
                    }
                })
                .switchIfEmpty(Mono.error(new RuntimeException("User not found with email: " + userToAuthenticate.getEmail())))
                .onErrorResume(error -> {
                    logger.error("Login failed for email: {}", userToAuthenticate.getEmail(), error);
                    return Mono.error(new RuntimeException("Login failed: " + error.getMessage()));
                });
    }
}