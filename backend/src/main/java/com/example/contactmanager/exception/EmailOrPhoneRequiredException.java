package com.example.contactmanager.exception;

/**
 * Thrown when registration is missing both email and phone (HTTP 400).
 */
public class EmailOrPhoneRequiredException extends RuntimeException {

    public EmailOrPhoneRequiredException(String message) {
        super(message);
    }

    public EmailOrPhoneRequiredException() {
        super("Either email or phone must be provided");
    }
}
