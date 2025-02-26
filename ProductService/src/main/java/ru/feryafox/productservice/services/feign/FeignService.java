package ru.feryafox.productservice.services.feign;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.feryafox.models.internal.responses.ShopInfoInternalResponse;
import ru.feryafox.productservice.feigns.ShopClient;

@Service
@RequiredArgsConstructor
public class FeignService {
    private final ShopClient shopClient;

    @Value("internal.api.key")
    private String apiKey;

    public ShopInfoInternalResponse getShopInfo(String shopId) {
        return shopClient.getShopInfo(shopId, apiKey);
    }
}
