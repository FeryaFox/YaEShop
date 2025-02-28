package ru.feryafox.reviewservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.feryafox.models.internal.responses.UserProfileResponse;
import ru.feryafox.reviewservice.feign.InternUserProfileClient;

@Service
@RequiredArgsConstructor
public class FeignService {
    private final InternUserProfileClient internUserProfileClient;

    @Value("internal.api.key")
    private String apiKey;

    public UserProfileResponse getUserProfile(String userProfile) {
        return internUserProfileClient.getUserProfileIntern(userProfile, apiKey);
    }
}
