package ru.feryafox.authservice.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.feryafox.jwt.JwtUtils;

@Configuration
public class jwtConfig {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.jwt-expiration-ms}")
    private int jwtExpirationMs;

    @Value("${jwt.refresh-token-expiration-ms}")
    private int refreshTokenExpirationMs;

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils(jwtSecret, jwtExpirationMs, refreshTokenExpirationMs);
    }
}
