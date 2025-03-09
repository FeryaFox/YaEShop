package ru.feryafox.authservice.services.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.UserEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    public void sendUserEvent(UserEvent userEvent) {
        String topic = "user-topic";
        String key = userEvent.getId();

        log.info("Отправка события в Kafka. Топик: {}, Ключ: {}, Данные: {}", topic, key, userEvent);

        kafkaTemplate.send(topic, key, userEvent)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Успешно отправлено событие в Kafka. Топик: {}, Ключ: {}, Оффсет: {}",
                                topic, key, result.getRecordMetadata().offset());
                    } else {
                        log.error("Ошибка при отправке события в Kafka. Топик: {}, Ключ: {}, Ошибка: {}",
                                topic, key, ex.getMessage(), ex);
                    }
                });
    }
}
