package ru.feryafox.cartservice.services.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.OrderEvent;
import ru.feryafox.kafka.models.ReviewEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, OrderEvent> orderKafkaTemplate;
    private final KafkaTemplate<String, ReviewEvent> reviewKafkaTemplate;

    public void sendCreateOrder(OrderEvent orderEvent) {
        String topic = "order-topic";
        String key = orderEvent.getOrderId();

        log.info("Отправка события создания заказа в Kafka. Топик: {}, Ключ: {}, Данные: {}", topic, key, orderEvent);

        orderKafkaTemplate.send(topic, key, orderEvent)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Заказ успешно отправлен в Kafka. Топик: {}, Ключ: {}, Оффсет: {}",
                                topic, key, result.getRecordMetadata().offset());
                    } else {
                        log.error("Ошибка при отправке заказа в Kafka. Топик: {}, Ключ: {}, Ошибка: {}",
                                topic, key, ex.getMessage(), ex);
                    }
                });
    }
}
