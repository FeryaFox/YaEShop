package ru.feryafox.paymentservice.exceptions;

public class PaymentException extends RuntimeException{
    public PaymentException(String message){
        super(message);
    }
}
