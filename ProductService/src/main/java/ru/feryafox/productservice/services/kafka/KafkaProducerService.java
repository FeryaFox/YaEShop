package ru.feryafox.productservice.services.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.ProductEvent;
import ru.feryafox.kafka.models.ShopRatingEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, ProductEvent> productKafkaTemplate;
    private final KafkaTemplate<String, ShopRatingEvent> shopRatingKafkaTemplate;

    public void sendProductEvent(ProductEvent productEvent) {
        String topic = "product-topic";
        String key = productEvent.getProductId();

        log.info("Отправка ProductEvent в Kafka. Топик: {}, Ключ: {}, Данные: {}", topic, key, productEvent);

        productKafkaTemplate.send(topic, key, productEvent)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("ProductEvent успешно отправлен. Топик: {}, Ключ: {}, Оффсет: {}",
                                topic, key, result.getRecordMetadata().offset());
                    } else {
                        log.error("Ошибка при отправке ProductEvent. Топик: {}, Ключ: {}, Ошибка: {}",
                                topic, key, ex.getMessage(), ex);
                    }
                });
    }

    public void sendShopRatingEvent(ShopRatingEvent shopRatingEvent) {
        String topic = "shop-rating-topic";
        String key = shopRatingEvent.getShopId();

        log.info("Отправка ShopRatingEvent в Kafka. Топик: {}, Ключ: {}, Данные: {}", topic, key, shopRatingEvent);

        shopRatingKafkaTemplate.send(topic, key, shopRatingEvent)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("ShopRatingEvent успешно отправлен. Топик: {}, Ключ: {}, Оффсет: {}",
                                topic, key, result.getRecordMetadata().offset());
                    } else {
                        log.error("Ошибка при отправке ShopRatingEvent. Топик: {}, Ключ: {}, Ошибка: {}",
                                topic, key, ex.getMessage(), ex);
                    }
                });
    }
}
