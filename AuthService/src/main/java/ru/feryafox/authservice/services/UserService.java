package ru.feryafox.authservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feryafox.authservice.entities.User;
import ru.feryafox.authservice.exceptions.UserIsExistException;
import ru.feryafox.authservice.models.requests.RegisterRequest;
import ru.feryafox.authservice.repositories.UserRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void register(RegisterRequest registerRequest) {
        if (userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())) {
            throw new UserIsExistException(registerRequest.getPhoneNumber());
        }

        User user = new User();
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setFirstName(registerRequest.getFirstName());
        user.setSurname(registerRequest.getSurname());
        user.setMiddleName(registerRequest.getMiddleName());
        user.setRoles(Set.of(registerRequest.getRole() == "BUYER" ? ));
    }
}
