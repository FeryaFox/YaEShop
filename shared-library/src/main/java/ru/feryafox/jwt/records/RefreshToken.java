package ru.feryafox.jwt.records;

import java.util.Date;

public record RefreshToken(String refreshToken, Date expiredAt) {
}
