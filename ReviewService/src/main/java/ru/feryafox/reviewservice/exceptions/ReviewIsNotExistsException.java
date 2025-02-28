package ru.feryafox.reviewservice.exceptions;

public class ReviewIsNotExistsException extends ReviewException {
    public ReviewIsNotExistsException(String reviewId) {
        super("Review " + reviewId + " is not exists");
    }
}
