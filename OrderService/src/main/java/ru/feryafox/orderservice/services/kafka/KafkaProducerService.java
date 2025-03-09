package ru.feryafox.orderservice.services.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.PaymentRequestEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, PaymentRequestEvent> kafkaTemplatePaymentRequestEvent;

    public void sendPaymentRequestEvent(PaymentRequestEvent paymentRequestEvent) {
        String topic = "payment-request-topic";
        String key = paymentRequestEvent.getOrderId();

        log.info("Отправка события PaymentRequestEvent в Kafka. Топик: {}, Ключ: {}, Данные: {}",
                topic, key, paymentRequestEvent);

        kafkaTemplatePaymentRequestEvent.send(topic, key, paymentRequestEvent)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Событие PaymentRequestEvent успешно отправлено. Топик: {}, Ключ: {}, Оффсет: {}",
                                topic, key, result.getRecordMetadata().offset());
                    } else {
                        log.error("Ошибка при отправке события PaymentRequestEvent. Топик: {}, Ключ: {}, Ошибка: {}",
                                topic, key, ex.getMessage(), ex);
                    }
                });
    }
}
