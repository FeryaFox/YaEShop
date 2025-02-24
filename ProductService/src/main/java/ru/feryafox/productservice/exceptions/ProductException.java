package ru.feryafox.productservice.exceptions;

public class ProductException extends RuntimeException {
    public ProductException(String message) {
        super(message);
    }
}
