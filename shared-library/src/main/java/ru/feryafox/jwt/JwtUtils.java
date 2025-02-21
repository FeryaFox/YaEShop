package ru.feryafox.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import ru.feryafox.jwt.dto.RefreshToken;

import java.security.Key;
import java.util.*;


public class JwtUtils extends JwtUtilsBase {
    private final long jwtExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtUtils(String jwtSecret, long jwtExpirationMs, long refreshTokenExpirationMs) {
        super(jwtSecret);
        this.jwtExpirationMs = jwtExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }


    /**
     * Генерация JWT-токена с UUID пользователя
     */
    public String generateToken(UUID userId, List<String> roles) {
        return Jwts.builder()
                .setSubject(userId.toString()) // Храним UUID в subject
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Генерация Refresh-токена с UUID и списком user-agents
     */
    public RefreshToken generateRefreshToken(UUID userId) {
        Date expiredAt = new Date(System.currentTimeMillis() + refreshTokenExpirationMs);
        String refreshToken = Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(expiredAt)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

        return new RefreshToken(refreshToken, expiredAt);
    }



    /**
     * Получаем токен из заголовка Authorization
     */
    public String getTokenFromHeader(String authHeader) {
        return authHeader.substring(7);
    }
}
