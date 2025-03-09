package ru.feryafox.authservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.feryafox.authservice.repositories.UserRepository;
import ru.feryafox.utils.UUIDUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        log.info("Попытка загрузки пользователя с идентификатором: {}", identifier);

        UserDetails userDetails;

        if (UUIDUtils.isUUID(identifier)) {
            log.info("Идентификатор {} распознан как UUID", identifier);
            userDetails = userRepository.findById(UUID.fromString(identifier))
                    .orElseThrow(() -> {
                        log.warn("Пользователь с ID {} не найден", identifier);
                        return new UsernameNotFoundException("Пользователь не найден по ID: " + identifier);
                    });
        } else {
            log.info("Идентификатор {} распознан как номер телефона", identifier);
            userDetails = userRepository.findByPhoneNumber(identifier)
                    .orElseThrow(() -> {
                        log.warn("Пользователь с номером {} не найден", identifier);
                        return new UsernameNotFoundException("Пользователь не найден по номеру телефона: " + identifier);
                    });
        }

        log.info("Пользователь {} успешно загружен", identifier);
        return userDetails;
    }
}
