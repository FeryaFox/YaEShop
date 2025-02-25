package ru.feryafox.productservice.exceptions;

public class NoAccessToTheProductException extends ProductException{
    public NoAccessToTheProductException(String productId, String userId) {
        super("No access to the product " + productId + " by user " + userId);
    }

}
