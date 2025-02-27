package ru.feryafox.reviewservice.exceptions;

public class ProductIsNotExistsException extends ReviewException{
    public ProductIsNotExistsException(String productId){
        super("The product " + productId + " is not exists");
    }
}
