package ru.feryafox.feign;

import feign.RequestInterceptor;

public class InternalFeignConfig {
    public static RequestInterceptor getRequestInterceptor(String internalApiKey) {
        return requestTemplate -> {
            requestTemplate.header("X-Internal-API-Key", internalApiKey);
        };
    }
}
