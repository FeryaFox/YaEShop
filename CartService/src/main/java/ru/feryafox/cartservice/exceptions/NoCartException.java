package ru.feryafox.cartservice.exceptions;

public class NoCartException extends CartException {
    public NoCartException(String userId) {
        super("No cart found for user " + userId);
    }
}
