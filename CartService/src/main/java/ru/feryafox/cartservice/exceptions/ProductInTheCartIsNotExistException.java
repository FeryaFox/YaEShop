package ru.feryafox.cartservice.exceptions;

public class ProductInTheCartIsNotExistException extends CartException {
    public ProductInTheCartIsNotExistException(String productId, String userId) {
        super("The product " + productId + " is not in the cart " + userId);
    }
}
