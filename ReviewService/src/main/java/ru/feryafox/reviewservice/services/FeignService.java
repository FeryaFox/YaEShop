package ru.feryafox.reviewservice.services;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.feryafox.models.internal.responses.UserProfileResponse;
import ru.feryafox.reviewservice.feign.InternUserProfileClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeignService {
    private final InternUserProfileClient internUserProfileClient;

    @Value("${internal.api.key}")
    private String apiKey;

    public UserProfileResponse getUserProfile(String userProfile) {
        log.info("Запрос информации о профиле пользователя {} через Feign", userProfile);
        try {
            UserProfileResponse response = internUserProfileClient.getUserProfileIntern(userProfile, apiKey);
            log.info("Успешно получена информация о профиле пользователя {}", userProfile);
            return response;
        } catch (FeignException e) {
            log.error("Ошибка при запросе профиля пользователя {} через Feign: {}", userProfile, e.getMessage(), e);
            throw e;
        }
    }
}
