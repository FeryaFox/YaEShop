package ru.feryafox.orderservice.exceptions;

public class OrderException extends RuntimeException{
    public OrderException(String message){
        super(message);
    }
}
