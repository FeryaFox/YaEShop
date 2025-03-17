package ru.feryafox.orderservice.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderIsNotExistsException extends OrderException {
    public OrderIsNotExistsException(String orderId) {
        super("Заказ с ID " + orderId + " не существует");
    }
}
