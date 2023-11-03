package com.github.saqie.mediatej.core.exception;

public class MediateJConflictException extends RuntimeException {

    public MediateJConflictException() {
    }

    public MediateJConflictException(String message) {
        super(message);
    }

    public MediateJConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
