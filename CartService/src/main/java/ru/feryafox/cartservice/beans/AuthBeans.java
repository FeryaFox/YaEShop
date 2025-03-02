package ru.feryafox.cartservice.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.feryafox.auth.JwtAuthorizationFilter;
import ru.feryafox.jwt.JwtUtilsBase;

@Configuration
public class AuthBeans {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public JwtUtilsBase jwtUtilsBase() {
        return new JwtUtilsBase(jwtSecret);
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtilsBase());
    }
}
