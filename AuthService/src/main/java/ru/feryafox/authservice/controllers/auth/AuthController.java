package ru.feryafox.authservice.controllers.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.authservice.models.requests.LoginRequest;
import ru.feryafox.authservice.models.requests.RefreshTokenRequest;
import ru.feryafox.authservice.models.requests.RegisterRequest;
import ru.feryafox.authservice.models.responses.AccessTokenResponse;
import ru.feryafox.authservice.models.responses.AuthResponse;
import ru.feryafox.authservice.services.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API для аутентификации пользователей")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Регистрация нового пользователя", responses = {
            @ApiResponse(responseCode = "204", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким номером уже существует", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest registerRequest) {
        System.out.println(registerRequest);
        authService.register(registerRequest);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Авторизация пользователя", responses = {
            @ApiResponse(responseCode = "200", description = "Успешная авторизация", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        AuthResponse authResponse = authService.login(loginRequest, request);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", authResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/auth")
                .maxAge(30 * 24 * 60 * 60)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString()).body(authResponse);
    }

    @Operation(summary = "Обновление токена доступа", responses = {
            @ApiResponse(responseCode = "200", description = "Токен успешно обновлен", content = @Content(schema = @Schema(implementation = AccessTokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Рефреш-токен отсутствует или недействителен", content = @Content)
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody(required = false) RefreshTokenRequest refreshTokenRequest,
                                          @CookieValue(name = "refresh_token", required = false) String refreshTokenFromCookie) {
        String refreshToken = (refreshTokenRequest != null && refreshTokenRequest.getRefreshToken() != null)
                ? refreshTokenRequest.getRefreshToken()
                : refreshTokenFromCookie;

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is missing");
        }

        AuthResponse authResponse = authService.refreshToken(refreshToken);

        if (refreshTokenFromCookie == null) {
            return ResponseEntity.ok(authResponse);
        }

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", authResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/auth")
                .maxAge(30 * 24 * 60 * 60)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(AccessTokenResponse.from(authResponse));
    }

    @Operation(summary = "Выход из системы", responses = {
            @ApiResponse(responseCode = "200", description = "Успешный выход из системы", content = @Content)
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody(required = false) RefreshTokenRequest refreshTokenRequest,
                                    @CookieValue(name = "refresh_token", required = false) String refreshTokenFromCookie) {
        String refreshToken = (refreshTokenRequest != null && refreshTokenRequest.getRefreshToken() != null)
                ? refreshTokenRequest.getRefreshToken()
                : refreshTokenFromCookie;

        if (refreshToken != null) {
            authService.logout(refreshToken);
        }

        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/auth")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body("Logged out successfully");
    }

    @Operation(summary = "Информация о сервисе", responses = {
            @ApiResponse(responseCode = "418", description = "Информация о сервисе", content = @Content)
    })
    @GetMapping("/about")
    public ResponseEntity<?> about() {
        return new ResponseEntity<>("Автор этого - FeryaFox", HttpStatusCode.valueOf(418));
    }
}
