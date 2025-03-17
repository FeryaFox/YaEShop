package ru.feryafox.kafka.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequestEvent implements BaseKafkaModel {
    private final String type = "PaymentRequestEvent";

    private String orderId;
    private String userId;
    private double totalPrice;

    @Override
    public String getType() {
        return type;
    }
}
