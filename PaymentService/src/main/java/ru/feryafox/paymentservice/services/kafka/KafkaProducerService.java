package ru.feryafox.paymentservice.services.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.PaymentResponseEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, PaymentResponseEvent> kafkaTemplate;

    public void send(PaymentResponseEvent event) {
        String topic = "payment-response-topic";
        String key = event.getPaymentId();

        log.info("Отправка события PaymentResponseEvent в Kafka. Топик: {}, Ключ: {}, Данные: {}",
                topic, key, event);

        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Событие PaymentResponseEvent успешно отправлено. Топик: {}, Ключ: {}, Оффсет: {}",
                                topic, key, result.getRecordMetadata().offset());
                    } else {
                        log.error("Ошибка при отправке события PaymentResponseEvent. Топик: {}, Ключ: {}, Ошибка: {}",
                                topic, key, ex.getMessage(), ex);
                    }
                });
    }
}
