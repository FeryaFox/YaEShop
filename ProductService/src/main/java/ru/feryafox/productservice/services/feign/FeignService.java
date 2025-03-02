package ru.feryafox.productservice.services.feign;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.feryafox.models.internal.requests.AddToCartRequest;
import ru.feryafox.models.internal.responses.ShopInfoInternalResponse;
import ru.feryafox.productservice.feigns.CartClient;
import ru.feryafox.productservice.feigns.ShopClient;

@Service
@RequiredArgsConstructor
public class FeignService {
    private final ShopClient shopClient;
    private final CartClient cartClient;

    @Value("internal.api.key")
    private String apiKey;

    public ShopInfoInternalResponse getShopInfo(String shopId) {
        return shopClient.getShopInfo(shopId, apiKey);
    }

    public void addToCart(
            String userId,
            int quantity,
            AddToCartRequest addToCartRequest
    ) {
        cartClient.addToCart(userId, quantity, addToCartRequest, apiKey);
    }
}
