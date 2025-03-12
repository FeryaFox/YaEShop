package ru.feryafox.authservice.models.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Запрос на обновление токена")
public class RefreshTokenRequest {

    @NotBlank(message = "Рефреш-токен обязателен")
    @Schema(description = "Рефреш-токен для обновления доступа", example = "eyJhbGciOiJIUzI1NiIsInR...")
    private String refreshToken;
}
