package ru.feryafox.notificationservice.servicies.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.NotificationEvent;
import ru.feryafox.kafka.models.UserEvent;
import ru.feryafox.notificationservice.servicies.NotificationService;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final KafkaService kafkaService;
    private final NotificationService notificationService;

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

    @KafkaListener(topics = "notification-topic")
    public void listenNotification(ConsumerRecord<String, Object> record) {
        Object event = record.value();

        if (event instanceof NotificationEvent notificationEvent) {
            notificationService.sendNotification(notificationEvent);
        }
        else {
            System.out.println("Пришло что-то не то...");
        }
    }
}
