package com.example.contactmanager.exception;

/**
 * Thrown when registering a user that already exists (HTTP 409).
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
