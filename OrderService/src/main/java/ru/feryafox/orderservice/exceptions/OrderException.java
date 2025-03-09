package ru.feryafox.orderservice.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderException extends RuntimeException{
    public OrderException(String message){
        super(message);
        log.error(message);
    }
}
