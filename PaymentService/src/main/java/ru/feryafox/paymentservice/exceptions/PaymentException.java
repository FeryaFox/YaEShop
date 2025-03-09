package ru.feryafox.paymentservice.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaymentException extends RuntimeException{
    public PaymentException(String message){
        super(message);
        log.error(message);
    }
}
