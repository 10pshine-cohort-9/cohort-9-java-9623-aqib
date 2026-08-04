package com.example.contactmanager.exception;

/**
 * Thrown when authentication is required but missing (HTTP 401).
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException() {
        super("Authentication is required to access this resource");
    }
}
