package ru.feryafox.reviewservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "auth-service")
public interface InternUserProfileClient {
    @GetMapping("/intern/auth/") // STOP HERE
}
