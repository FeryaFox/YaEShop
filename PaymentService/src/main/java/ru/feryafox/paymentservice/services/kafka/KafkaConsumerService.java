package ru.feryafox.paymentservice.services.kafka;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.PaymentRequestEvent;
import ru.feryafox.paymentservice.services.PaymentService;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final PaymentService paymentService;

    @KafkaListener(topics = "payment-request-topic")
    public void listen(ConsumerRecord<String, Object> record) {
        Object event = record.value();

        if (event instanceof PaymentRequestEvent paymentRequestEvent) {
            paymentService.createPaymentFromEvent(paymentRequestEvent);
        } else {
            System.out.println("Received unknown event: " + event);
        }
    }
}
