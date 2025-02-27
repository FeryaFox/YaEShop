package ru.feryafox.reviewservice.exceptions;

public class ReviewAlreadyExistsException extends ReviewException {
    public ReviewAlreadyExistsException(String productId, String shopId, String userId) {
        super("Product " + productId + " already exists in shop " + shopId + " by user " + userId);
    }
}
