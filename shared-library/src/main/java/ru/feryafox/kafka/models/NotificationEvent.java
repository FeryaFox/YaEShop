package ru.feryafox.kafka.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationEvent implements BaseKafkaModel{
    private final String type = "NotificationEvent";

    private String userId;
    private String message;
}
