package ru.feryafox.kafka.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationEvent implements BaseKafkaModel{
    private final String type = "NotificationEvent";

    private String userId;
    private String message;

    @Override
    public String getType() {
        return type;
    }
}
