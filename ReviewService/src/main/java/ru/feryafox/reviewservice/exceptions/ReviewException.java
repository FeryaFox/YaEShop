package ru.feryafox.reviewservice.exceptions;

public class ReviewException extends RuntimeException {
    public ReviewException(String message) {
        super(message);
    }
}
