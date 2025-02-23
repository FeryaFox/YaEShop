package ru.feryafox.productservice.services.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.ShopEvent;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "shop-topic", groupId = "product-service-group")
    public void listen(ConsumerRecord<String, ShopEvent> record) {
        System.out.println("✅ Получено сообщение: " + record.value());
    }
}
