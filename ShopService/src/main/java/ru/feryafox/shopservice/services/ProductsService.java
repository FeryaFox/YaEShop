package ru.feryafox.shopservice.services;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.feryafox.models.internal.responses.ProductInfoInternResponse;
import ru.feryafox.shopservice.services.feign.FeignService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductsService {
    private final FeignService feignService;

    public List<ProductInfoInternResponse> getInternProducts(String shopId, int page, int size) {
        log.info("Запрос товаров для магазина {} через Feign (page={}, size={})", shopId, page, size);
        try {
            List<ProductInfoInternResponse> products = feignService.getProducts(shopId, page, size);
            log.info("Успешно получены товары для магазина {} ({} шт.)", shopId, products.size());
            return products;
        } catch (FeignException e) {
            log.error("Ошибка при запросе товаров для магазина {} через Feign: {}", shopId, e.getMessage(), e);
            throw e;
        }
    }
}
