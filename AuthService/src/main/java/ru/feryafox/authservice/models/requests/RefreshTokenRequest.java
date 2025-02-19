package ru.feryafox.authservice.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshTokenRequest {
    private String refreshToken;
}
