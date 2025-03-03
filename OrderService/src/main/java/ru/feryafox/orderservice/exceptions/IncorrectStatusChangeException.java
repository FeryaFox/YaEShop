package ru.feryafox.orderservice.exceptions;

import ru.feryafox.orderservice.entities.Order;

public class IncorrectStatusChangeException extends OrderException{
    public IncorrectStatusChangeException(Order.OrderStatus oldStatus, Order.OrderStatus newStatus) {
        super("Can't change order status from " + oldStatus + " to " + newStatus);
    }
}
