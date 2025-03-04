package ru.feryafox.paymentservice.services.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.PaymentResponseEvent;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, PaymentResponseEvent> kafkaTemplate;

    public void send(PaymentResponseEvent event) {
        kafkaTemplate.send("payment-response-topic", event.getPaymentId(), event);
    }
}
