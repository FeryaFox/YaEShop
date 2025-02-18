package ru.feryafox.gatewayservice.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RouteLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RouteLoggingFilter.class);

    public RouteLoggingFilter() {
        logger.info("RouteLoggingFilter initialized");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String originalUri = request.getURI().toString();
        String targetUri = exchange.getAttribute("org.springframework.cloud.gateway.support.ServerWebExchangeUtils.gatewayRequestUrl");

        logger.info("Incoming request: {} {}", request.getMethod(), originalUri);
        logger.info("Routing request to: {}", targetUri != null ? targetUri : "UNKNOWN");

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            logger.info("Response status: {} for {}", response.getStatusCode(), originalUri);
        }));
    }

    @Override
    public int getOrder() {
        return -1; // Выполняется перед другими фильтрами
    }
}
