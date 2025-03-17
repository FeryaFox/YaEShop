package ru.feryafox.notificationservice.servicies.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.NotificationEvent;
import ru.feryafox.kafka.models.UserEvent;
import ru.feryafox.notificationservice.servicies.NotificationService;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final KafkaService kafkaService;
    private final NotificationService notificationService;

    @KafkaListener(topics = "user-topic")
    public void listenUserEvents(ConsumerRecord<String, Object> record) {
        log.info("Получено сообщение из Kafka (топик user-topic): {}", record.value());

        Object event = record.value();
        if (event instanceof UserEvent userEvent) {
            log.info("Обрабатываем событие UserEvent с ID: {}", userEvent.getId());
            kafkaService.processUserEvent(userEvent);
        } else {
            log.warn("Получено неизвестное сообщение в топике user-topic: {}", event);
        }
    }

    @KafkaListener(topics = "notification-topic")
    public void listenNotificationEvents(ConsumerRecord<String, Object> record) {
        log.info("Получено сообщение из Kafka (топик notification-topic): {}", record.value());

        Object event = record.value();
        if (event instanceof NotificationEvent notificationEvent) {
            log.info("Обрабатываем событие NotificationEvent для пользователя: {}", notificationEvent.getUserId());
            notificationService.sendNotification(notificationEvent);
        } else {
            log.warn("Получено неизвестное сообщение в топике notification-topic: {}", event);
        }
    }
}
