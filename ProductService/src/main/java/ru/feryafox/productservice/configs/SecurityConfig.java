package ru.feryafox.productservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import ru.feryafox.auth.CorsConfig;
import ru.feryafox.auth.JwtAuthorizationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JwtAuthorizationFilter jwtAuthorizationFilter) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                        .requestMatchers(
                                "/product/swagger-ui/**",
                                "/product/v3/api-docs/**",
                                "/product/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/product/{productId}", "/product/shop/{shopId}", "/product/search").permitAll()

                        .requestMatchers("/product/{productId}/add_to_cart").hasRole("BUYER")
                        .requestMatchers("/intern/product/**").permitAll()
                        .requestMatchers("/product/**").hasRole("SELLER")
                )
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return CorsConfig.getCorsConfig();
    }
}
