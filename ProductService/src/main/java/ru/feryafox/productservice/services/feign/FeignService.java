package ru.feryafox.productservice.services.feign;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.feryafox.models.internal.requests.AddToCartRequest;
import ru.feryafox.models.internal.responses.ShopInfoInternalResponse;
import ru.feryafox.productservice.feigns.CartClient;
import ru.feryafox.productservice.feigns.ShopClient;
import feign.FeignException;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeignService {
    private final ShopClient shopClient;
    private final CartClient cartClient;

    @Value("${internal.api.key}")
    private String apiKey;

    public ShopInfoInternalResponse getShopInfo(String shopId) {
        log.info("Запрос информации о магазине {} через Feign", shopId);
        try {
            ShopInfoInternalResponse response = shopClient.getShopInfo(shopId, apiKey);
            log.info("Успешно получена информация о магазине {}", shopId);
            return response;
        } catch (FeignException e) {
            log.error("Ошибка при запросе информации о магазине {} через Feign: {}", shopId, e.getMessage(), e);
            throw e;
        }
    }

    public void addToCart(String userId, int quantity, AddToCartRequest addToCartRequest) {
        log.info("Добавление продукта {} в корзину пользователя {} (количество: {}) через Feign",
                addToCartRequest.getProductId(), userId, quantity);
        try {
            cartClient.addToCart(userId, quantity, addToCartRequest, apiKey);
            log.info("Продукт {} успешно добавлен в корзину пользователя {}", addToCartRequest.getProductId(), userId);
        } catch (FeignException e) {
            log.error("Ошибка при добавлении продукта {} в корзину пользователя {} через Feign: {}",
                    addToCartRequest.getProductId(), userId, e.getMessage(), e);
            throw e;
        }
    }
}
