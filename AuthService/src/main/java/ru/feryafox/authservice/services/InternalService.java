package ru.feryafox.authservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feryafox.authservice.entities.User;
import ru.feryafox.authservice.repositories.UserRepository;
import ru.feryafox.models.internal.responses.UserProfileResponse;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InternalService {
    private final UserRepository userRepository;

    public UserProfileResponse getBuyerProfileServiceByUser(String userId) {
        User user = userRepository.findById(UUID.fromString(userId)).get();

        UserProfileResponse.UserProfileResponseBuilder builder = UserProfileResponse.builder();

        builder.id(user.getId().toString());
        builder.phone(user.getPhoneNumber());
        builder.firstName(user.getFirstName());
        builder.surname(user.getSurname());
        builder.middleName(user.getMiddleName());

        return builder.build();
    }
}
