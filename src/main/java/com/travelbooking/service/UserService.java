package com.travelbooking.service;

import com.travelbooking.model.User;
import com.travelbooking.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Register a new user
    public Mono<User> registerUser(User user) {
        logger.info("Registering user: {}", user.getEmail());
        return userRepository.save(user)
                .doOnSuccess(savedUser -> logger.info("User registered successfully: {}", savedUser.getEmail()))
                .doOnError(error -> logger.error("Error occurred while registering user: {}", user.getEmail(), error));
    }

    // Find a user by email
    public Mono<User> findByEmail(String email) {
        logger.info("Searching for user by email: {}", email);
        return userRepository.findByEmail(email)
                .doOnSuccess(foundUser -> {
                    if (foundUser != null) {
                        logger.info("User found with email: {}", email);
                    } else {
                        logger.warn("No user found with email: {}", email);
                    }
                })
                .doOnError(error -> logger.error("Error occurred while searching for user with email: {}", email, error));
    }

    // Find a user by ID
    public Mono<User> findById(String id) {
        logger.info("Searching for user by ID: {}", id);
        return userRepository.findById(id)
                .doOnSuccess(foundUser -> {
                    if (foundUser != null) {
                        logger.info("User found with ID: {}", id);
                    } else {
                        logger.warn("No user found with ID: {}", id);
                    }
                })
                .doOnError(error -> logger.error("Error occurred while searching for user with ID: {}", id, error));
    }

    // Fetch all users
    public Flux<User> getAllUsers() {
        logger.info("Fetching all users...");
        return userRepository.findAll()
                .doOnComplete(() -> logger.info("Successfully fetched all users"))
                .doOnError(error -> logger.error("Error occurred while fetching all users", error));
    }

    // Update an existing user
    public Mono<User> updateUser(String id, User updatedUser) {
        logger.info("Updating user with ID: {}", id);
        return userRepository.findById(id)
                .flatMap(existingUser -> {
                    existingUser.setEmail(updatedUser.getEmail());
                    existingUser.setPassword(updatedUser.getPassword());
                    existingUser.setUsername(updatedUser.getUsername());
                    existingUser.setRole(updatedUser.getRole());
                    return userRepository.save(existingUser);
                })
                .doOnSuccess(updatedUserDetails -> logger.info("User updated successfully: {}", id))
                .doOnError(error -> logger.error("Error occurred while updating user with ID: {}", id, error));
    }

    // Delete a user by ID
    public Mono<Void> deleteUser(String id) {
        logger.info("Deleting user with ID: {}", id);
        return userRepository.deleteById(id)
                .doOnSuccess(unused -> logger.info("User deleted successfully with ID: {}", id))
                .doOnError(error -> logger.error("Error occurred while deleting user with ID: {}", id, error));
    }
}
