package ru.feryafox.reviewservice.services.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.ReviewEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, ReviewEvent> kafkaTemplate;

    public void sendReviewUpdate(ReviewEvent event) {
        String topic = "review-topic";
        String key = event.getReviewId();

        log.info("Отправка ReviewEvent в Kafka. Топик: {}, Ключ: {}, Данные: {}", topic, key, event);

        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("ReviewEvent успешно отправлен. Топик: {}, Ключ: {}, Оффсет: {}",
                                topic, key, result.getRecordMetadata().offset());
                    } else {
                        log.error("Ошибка при отправке ReviewEvent. Топик: {}, Ключ: {}, Ошибка: {}",
                                topic, key, ex.getMessage(), ex);
                    }
                });
    }
}
