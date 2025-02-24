package ru.feryafox.productservice.services.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.ShopEvent;
import ru.feryafox.productservice.entities.mongo.Shop;
import ru.feryafox.productservice.repositories.mongo.ShopRepository;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ShopRepository shopRepository;

    @KafkaListener(topics = "shop-topic", groupId = "product-service-group")
    public void listen(ConsumerRecord<String, Object> record) {

        Object event = record.value();

        if (event instanceof ShopEvent shopEvent) {
            Shop shop = new Shop();
            shop.setId(shopEvent.getShopId());
            shop.setShopName(shopEvent.getShopName());
            shop.setUserOwner(shopEvent.getOwnerId());

            shopRepository.save(shop);

            System.out.println("✅ Получено ShopEvent: " + shopEvent);
        } else {
            System.out.println("⚠️ Получен неизвестный тип события: " + event);
        }
    }
}
