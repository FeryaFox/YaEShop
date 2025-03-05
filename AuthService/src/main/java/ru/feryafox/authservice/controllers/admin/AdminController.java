package ru.feryafox.authservice.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.feryafox.authservice.models.requests.RegisterRequestDelivery;
import ru.feryafox.authservice.services.AuthService;

@RestController
@RequestMapping("/admin/auth/")
@RequiredArgsConstructor
public class AdminController {
    private final AuthService authService;

    @PostMapping("create_delivery")
    public ResponseEntity<?> createDelivery(
            @RequestBody RegisterRequestDelivery request
    ) {
        authService.registerDelivery(request);
        return ResponseEntity.noContent().build();
    }
}
