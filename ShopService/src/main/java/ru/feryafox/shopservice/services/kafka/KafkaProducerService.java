package ru.feryafox.shopservice.services.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.ShopEvent;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, ShopEvent> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, ShopEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendShopUpdate(ShopEvent store) {
        kafkaTemplate.send("shop-topic", store.getShopId(), store);
    }
}
