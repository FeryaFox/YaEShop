package ru.feryafox.cartservice.services.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.OrderEvent;
import ru.feryafox.kafka.models.ReviewEvent;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void sendCreateOrder(OrderEvent orderEvent) {
        kafkaTemplate.send("order-topic", orderEvent.getOrderId(), orderEvent);
    }
}
