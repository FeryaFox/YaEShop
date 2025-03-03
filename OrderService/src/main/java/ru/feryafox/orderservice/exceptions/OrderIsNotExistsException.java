package ru.feryafox.orderservice.exceptions;

public class OrderIsNotExistsException extends OrderException {
    public OrderIsNotExistsException(String orderId) {
        super("Order with id " + orderId + " is not exists");
    }
}
