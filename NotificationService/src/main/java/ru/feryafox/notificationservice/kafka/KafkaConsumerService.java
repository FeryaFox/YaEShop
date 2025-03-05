package ru.feryafox.notificationservice.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.UserEvent;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final KafkaService kafkaService;

    @KafkaListener(topics = "user-topic")
    public void listen(ConsumerRecord<String, Object> record) {
        Object event = record.value();

        if (event instanceof UserEvent userEvent) {
            kafkaService.processUserEvent(userEvent);
        }
        else {
            System.out.println("Что-то пришло не то...");
        }
    }
}
