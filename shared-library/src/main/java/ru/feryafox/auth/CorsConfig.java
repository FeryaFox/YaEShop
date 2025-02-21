package ru.feryafox.auth;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

public final class CorsConfig {
    private CorsConfig() {}

    public static CorsConfigurationSource getCorsConfig() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*"); // Разрешаем все источники
        configuration.addAllowedMethod("*"); // Разрешаем все методы
        configuration.addAllowedHeader("*"); // Разрешаем все заголовки
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
