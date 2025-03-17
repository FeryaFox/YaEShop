package ru.feryafox.shopservice.services.feign;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.feryafox.models.internal.responses.ProductInfoInternResponse;
import ru.feryafox.shopservice.feign.InternProductClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeignService {
    @Value("${internal.api.key}")
    private String apiKey;

    private final InternProductClient internProductClient;

    public List<ProductInfoInternResponse> getProducts(String shopId, int page, int size) {
        log.info("Запрос списка товаров для магазина {} через Feign (page={}, size={})", shopId, page, size);
        try {
            List<ProductInfoInternResponse> response = internProductClient.getProductsByShopId(shopId, page, size, apiKey);
            log.info("Успешно получены товары для магазина {}", shopId);
            return response;
        } catch (FeignException e) {
            log.error("Ошибка при запросе товаров для магазина {} через Feign: {}", shopId, e.getMessage(), e);
            throw e;
        }
    }
}
