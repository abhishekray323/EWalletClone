package com.example.User.customExceptions;

public class UserAlreadyExistsException extends RuntimeException {
    /**
     * Exception thrown when a user already exists in the system.
     * @param username
     * @param email
     */
    public UserAlreadyExistsException(String username, String email) {
        super("User already exists username: " + username + " email: " + email);
    }
}
