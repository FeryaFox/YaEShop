package ru.feryafox.paymentservice.services.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.PaymentResponseEvent;
import ru.feryafox.paymentservice.entities.Payment;

@Service
@RequiredArgsConstructor
public class KafkaService {
    private final KafkaProducerService kafkaProducerService;

    public void sendResponse(Payment payment) {
        var paymentResponseEvent = PaymentResponseEvent.builder()
                .paymentId(payment.getId().toString())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId().toString())
                .totalPrice(payment.getTotalPrice().doubleValue())
                .build();

        kafkaProducerService.send(paymentResponseEvent);
    }
}
