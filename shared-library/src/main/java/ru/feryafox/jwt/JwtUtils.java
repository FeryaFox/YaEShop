package ru.feryafox.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import ru.feryafox.jwt.dto.RefreshToken;

import java.security.Key;
import java.util.*;


public class JwtUtils {
    private final String jwtSecret;
    private final long jwtExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtUtils(String jwtSecret, long jwtExpirationMs, long refreshTokenExpirationMs) {
        this.jwtSecret = jwtSecret;
        this.jwtExpirationMs = jwtExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Генерация JWT-токена с UUID пользователя
     */
    public String generateToken(UUID userId) {
        return Jwts.builder()
                .setSubject(userId.toString()) // Храним UUID в subject
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
     * Извлекаем UUID пользователя из токена
     */
    public UUID getUserIdFromToken(String token) {
        String userIdStr = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        return UUID.fromString(userIdStr);
    }


    /**
     * Получаем UUID пользователя из истекшего токена
     */
    public UUID getUserIdFromExpiredToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return UUID.fromString(claims.getSubject());
        } catch (ExpiredJwtException e) {
            return UUID.fromString(e.getClaims().getSubject());
        } catch (JwtException e) {
            throw e;
        }
    }

    /**
     * Валидация токена
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (MalformedJwtException e) {
            throw e;
        } catch (UnsupportedJwtException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    /**
     * Получаем токен из заголовка Authorization
     */
    public String getTokenFromHeader(String authHeader) {
        return authHeader.substring(7);
    }
}
