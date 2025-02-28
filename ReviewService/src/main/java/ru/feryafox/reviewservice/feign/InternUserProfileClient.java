package ru.feryafox.reviewservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.feryafox.models.internal.responses.UserProfileResponse;

@FeignClient(name = "auth-service")
public interface InternUserProfileClient {
    @GetMapping("/intern/auth/profile/buyer/{userId}") // STOP HERE
    UserProfileResponse getUserProfileIntern(
            @PathVariable("userId") String userId,
            @RequestHeader("X-Internal-API-Key") String apiKey
    );
}
