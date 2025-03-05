package ru.feryafox.kafka;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import ru.feryafox.kafka.models.NotificationEvent;

@AllArgsConstructor
public class NotificationService {
    private KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public void sendNotification(String userId, String message) {
        NotificationEvent event = new NotificationEvent(userId, message);
        kafkaTemplate.send("notification-topic", userId, event);
    }

}
