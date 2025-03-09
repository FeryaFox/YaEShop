package ru.feryafox.shopservice.services.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.ShopEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, ShopEvent> kafkaTemplate;

    public void sendShopUpdate(ShopEvent store) {
        String topic = "shop-topic";
        String key = store.getShopId();

        log.info("Отправка ShopEvent в Kafka. Топик: {}, Ключ: {}, Данные: {}", topic, key, store);

        kafkaTemplate.send(topic, key, store)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("ShopEvent успешно отправлен. Топик: {}, Ключ: {}, Оффсет: {}",
                                topic, key, result.getRecordMetadata().offset());
                    } else {
                        log.error("Ошибка при отправке ShopEvent. Топик: {}, Ключ: {}, Ошибка: {}",
                                topic, key, ex.getMessage(), ex);
                    }
                });
    }
}
