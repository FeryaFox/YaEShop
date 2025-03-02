package ru.feryafox.cartservice.exceptions;

public class CartException extends RuntimeException {
    public CartException(String message) {
        super(message);
    }
}
