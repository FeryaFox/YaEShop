package ru.feryafox.paymentservice.exceptions;

public class PaymentIsNotExistsException extends PaymentException {
    public PaymentIsNotExistsException(String paymentId) {
        super("Payment " + paymentId + "is not exists");
    }

}
