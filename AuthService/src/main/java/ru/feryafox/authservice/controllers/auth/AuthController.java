package ru.feryafox.authservice.controllers.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.authservice.models.requests.LoginRequest;
import ru.feryafox.authservice.models.requests.RegisterRequest;
import ru.feryafox.authservice.models.responses.AuthResponse;
import ru.feryafox.authservice.services.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
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
        authService.register(registerRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request
    ) {
        AuthResponse authResponse = authService.login(loginRequest, request);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", authResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false) // TODO потом поменять на true
                .path("/auth/refresh")
                .maxAge(30 * 24 * 60 * 60)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString()).body(authResponse);
    }

    @GetMapping("/about")
    public ResponseEntity<?> about() {
        return new ResponseEntity<>("Автор этого - FeryaFox", HttpStatusCode.valueOf(418));
    }
}