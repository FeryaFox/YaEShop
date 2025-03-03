package ru.feryafox.orderservice.services.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.PaymentRequestEvent;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, PaymentRequestEvent> kafkaTemplatePaymentRequestEvent;

    public void sendPaymentRequestEvent(PaymentRequestEvent paymentRequestEvent) {
        kafkaTemplatePaymentRequestEvent.send("payment-request-topic", paymentRequestEvent.getOrderId(), paymentRequestEvent);
    }
}
