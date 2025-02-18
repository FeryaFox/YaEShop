package ru.feryafox.authservice.controllers.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.authservice.models.requests.RegisterRequest;
import ru.feryafox.authservice.services.UserService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
//    @PostMapping("/login")
//    public ResponseEntity<?> login(
//            @RequestHeader(value = "User-Agent", required = false) String userAgent,
//            @RequestBody LoginRequest loginRequest,
//            HttpServletRequest request
//    ) throws MissingUserAgentException {
//        return ResponseEntity.ok(userService.login(loginRequest, userAgent, request));
//    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest registerRequest
    ) {
        userService.register(registerRequest);
        return ResponseEntity.ok().build();
    }
}