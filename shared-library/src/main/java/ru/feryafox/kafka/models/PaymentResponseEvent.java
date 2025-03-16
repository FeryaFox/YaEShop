package ru.feryafox.kafka.models;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponseEvent implements BaseKafkaModel {
    private final String type = "PaymentResponseEvent";

    private String orderId;
    private String paymentId;
    private String userId;
    private double totalPrice;

    @Override
    public String getType() {
        return type;
    }
}
