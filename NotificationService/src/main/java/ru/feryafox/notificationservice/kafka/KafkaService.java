package ru.feryafox.notificationservice.kafka;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.UserEvent;
import ru.feryafox.notificationservice.entities.User;
import ru.feryafox.notificationservice.repositories.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaService {
    private final UserRepository userRepository;

    @Transactional
    public void processUserEvent(UserEvent userEvent) {
        User user;
        switch (userEvent.getStatus()) {
           case CREATED:
               user = User.builder()
                       .id(UUID.fromString(userEvent.getId()))
                       .phoneNumber(userEvent.getPhoneNumber())
                       .firstName(userEvent.getFirstName())
                       .surname(userEvent.getSurname())
                       .middleName(userEvent.getMiddleName())
                       .build();

               userRepository.save(user);
               break;
           case UPDATED:
               user = userRepository.findById(UUID.fromString(userEvent.getId())).get();
               user.setPhoneNumber(userEvent.getPhoneNumber());
               user.setFirstName(userEvent.getFirstName());
               user.setSurname(userEvent.getSurname());
               user.setMiddleName(userEvent.getMiddleName());
               userRepository.save(user);
               break;

        }
    }
}
