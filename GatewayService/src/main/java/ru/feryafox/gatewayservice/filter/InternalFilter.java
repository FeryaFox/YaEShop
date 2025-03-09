package ru.feryafox.gatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class InternalFilter extends AbstractGatewayFilterFactory<InternalFilter.Config> {

    @Value("${internal.api.key}")
    private String internalApiKey;

    public InternalFilter() {
        super(Config.class);
    }

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String requestPath = request.getURI().getPath();

            if (requestPath.startsWith("/internal/")) {
                log.info("Запрос к внутреннему API: {}", requestPath);

                List<String> apiKeys = request.getHeaders().get("X-Internal-API-Key");
                if (apiKeys == null || apiKeys.isEmpty()) {
                    log.warn("Отсутствует заголовок X-Internal-API-Key. Доступ запрещен.");
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }

                String providedApiKey = apiKeys.get(0);
                if (!internalApiKey.equals(providedApiKey)) {
                    log.warn("Неверный API-ключ. Доступ запрещен.");
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }

                log.info("Доступ к {} разрешен", requestPath);
            }

            return chain.filter(exchange);
        };
    }
}
