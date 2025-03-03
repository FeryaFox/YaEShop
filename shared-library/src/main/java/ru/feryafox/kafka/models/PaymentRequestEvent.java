package ru.feryafox.kafka.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequestEvent implements BaseKafkaModel {
    private final String type = "PaymentRequestEvent";

    private String orderId;
    private String userId;
    private double totalPrice;
}
