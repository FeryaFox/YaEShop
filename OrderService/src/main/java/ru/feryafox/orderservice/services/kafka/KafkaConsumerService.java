package ru.feryafox.orderservice.services.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.OrderEvent;
import ru.feryafox.orderservice.services.OrderService;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final OrderService orderService;

    @KafkaListener(topics = "order-topic")
    public void listenCreateOrder(ConsumerRecord<String, Object> record) {
        Object event = record.value();

        if (event instanceof OrderEvent orderEvent) {
            orderService.createOrder(orderEvent);
        } else {
            System.out.println("Пришло что-то не то");
        }
    }
}
