package ru.feryafox.paymentservice.services.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.PaymentRequestEvent;
import ru.feryafox.paymentservice.services.PaymentService;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final PaymentService paymentService;

    @KafkaListener(topics = "payment-request-topic")
    public void listen(ConsumerRecord<String, Object> record) {
        log.info("Получено сообщение из Kafka (топик payment-request-topic): {}", record.value());

        Object event = record.value();
        if (event instanceof PaymentRequestEvent paymentRequestEvent) {
            log.info("Обрабатываем PaymentRequestEvent для заказа {}", paymentRequestEvent.getOrderId());
            paymentService.createPaymentFromEvent(paymentRequestEvent);
        } else {
            log.warn("Получено некорректное сообщение в payment-request-topic: {}", event);
        }
    }
}
