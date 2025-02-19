package ru.feryafox.authservice.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccessTokenResponse {
    private String accessToken;

    public static AccessTokenResponse from(AuthResponse authResponse) {
        return new AccessTokenResponse(authResponse.getAccessToken());
    }
}
