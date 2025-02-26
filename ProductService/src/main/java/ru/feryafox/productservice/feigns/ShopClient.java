package ru.feryafox.productservice.feigns;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.feryafox.models.internal.responses.ShopInfoInternalResponse;

@FeignClient(name = "shop-service")
public interface ShopClient {
    @GetMapping("/internal/shop/{shopId}")
    ShopInfoInternalResponse getShopInfo(
            @PathVariable("shopId") String shopId,
            @RequestHeader("X-Internal-API-Key") String apiKey
    );
}
