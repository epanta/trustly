package com.trustly.challenge.exception;

public class ProjectException extends RuntimeException {

    public ProjectException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
