package ru.feryafox.authservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feryafox.authservice.entities.User;
import ru.feryafox.authservice.exceptions.user.UserIsNotExistException;
import ru.feryafox.authservice.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class BaseService {
    private final UserRepository userRepository;

    public User getUser(String phone) {
       return userRepository.findByPhoneNumber(phone).orElseThrow(() -> new UserIsNotExistException(phone));
    }
}
