package ru.feryafox.shopservice.services.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.ShopRatingEvent;
import ru.feryafox.shopservice.services.ShopService;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final ShopService shopService;

    @KafkaListener(topics = "shop-rating-topic", groupId = "shop-service-group")
    public void listen(ConsumerRecord<String, Object> record) {
        log.info("Получено сообщение из Kafka (топик shop-rating-topic): {}", record.value());

        Object event = record.value();
        if (event instanceof ShopRatingEvent shopRatingEvent) {
            log.info("Обновление рейтинга магазина {} с рейтингом {}", shopRatingEvent.getShopId(), shopRatingEvent.getShopRating());
            shopService.updateShopRatingFromEvent(shopRatingEvent);
            log.info("Рейтинг магазина {} успешно обновлен", shopRatingEvent.getShopId());
        } else {
            log.warn("Получен неизвестный тип события в shop-rating-topic: {}", event);
        }
    }
}
