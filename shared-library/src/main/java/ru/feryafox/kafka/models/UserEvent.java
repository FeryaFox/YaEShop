package ru.feryafox.kafka.models;

import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEvent implements BaseKafkaModel {
    private final String type = "UserEvent";
    private String id;
    private String phoneNumber;
    private String firstName;
    private String surname;
    private String middleName;
    private Set<String> roles = new HashSet<>();
    private Status status;

    public enum Status {
        CREATED,
        UPDATED,
        DELETED
    }
}
