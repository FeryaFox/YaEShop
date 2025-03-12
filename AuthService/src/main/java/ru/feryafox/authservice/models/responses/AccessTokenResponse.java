package ru.feryafox.authservice.models.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Ответ с новым access-токеном")
public class AccessTokenResponse {

    @Schema(description = "Новый access-токен", example = "eyJhbGciOiJIUzI1NiIsInR...")
    private String accessToken;

    public static AccessTokenResponse from(AuthResponse authResponse) {
        return new AccessTokenResponse(authResponse.getAccessToken());
    }
}
