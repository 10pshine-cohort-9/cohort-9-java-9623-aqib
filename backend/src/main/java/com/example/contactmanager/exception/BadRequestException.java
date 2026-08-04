package com.example.contactmanager.exception;

/**
 * Thrown when a request is malformed or semantically invalid (HTTP 400).
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
