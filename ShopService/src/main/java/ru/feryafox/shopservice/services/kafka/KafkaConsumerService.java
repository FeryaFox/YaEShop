package ru.feryafox.shopservice.services.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.ShopRatingEvent;
import ru.feryafox.shopservice.services.ShopService;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final ShopService shopService;

    @KafkaListener(topics = "shop-rating-topic", groupId = "shop-service-group")
    public void listen(ConsumerRecord<String, Object> record) {
        Object event = record.value();

        if (event instanceof ShopRatingEvent shopRatingEvent) {
            shopService.updateShopRatingFromEvent(shopRatingEvent);
            System.out.println("Обновлена оценка для " + shopRatingEvent.getShopId());
        } else {
            System.out.println("⚠️ Получен неизвестный тип события: " + event);
        }
    }
}
