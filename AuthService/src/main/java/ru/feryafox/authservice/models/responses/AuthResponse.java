package ru.feryafox.authservice.models.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Ответ с токенами после аутентификации")
public class AuthResponse {

    @Schema(description = "Access-токен", example = "eyJhbGciOiJIUzI1NiIsInR...")
    private String accessToken;

    @Schema(description = "Refresh-токен", example = "eyJhbGciOiJIUzI1NiIsInR...")
    private String refreshToken;
}
