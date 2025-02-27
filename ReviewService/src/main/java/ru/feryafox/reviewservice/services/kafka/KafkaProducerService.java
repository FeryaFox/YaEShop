package ru.feryafox.reviewservice.services.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.ReviewEvent;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, ReviewEvent> kafkaTemplate;

    public void sendReviewUpdate(ReviewEvent event) {
        kafkaTemplate.send("review-topic", event.getReviewId(), event);
    }
}
