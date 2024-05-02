package com.kyraymege.StorEge.exceptions;

public class APIException extends RuntimeException {
    public APIException(String message) {
        super(message);
    }

    public APIException() {
        super("An error occurred. Please try again.");
    }
}
