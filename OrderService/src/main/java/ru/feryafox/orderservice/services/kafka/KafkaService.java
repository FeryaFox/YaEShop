package ru.feryafox.orderservice.services.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.PaymentRequestEvent;
import ru.feryafox.orderservice.entities.Order;

@Service
@RequiredArgsConstructor
public class KafkaService {
    private final KafkaProducerService kafkaProducerService;

    public void createPaymentRequest(Order order) {
        var paymentRequest = PaymentRequestEvent.builder()
                .orderId(order.getId().toString())
                .userId(order.getUserId().toString())
                .totalPrice(order.getTotalPrice().doubleValue())
                .build();

        kafkaProducerService.sendPaymentRequestEvent(paymentRequest);
    }
}
