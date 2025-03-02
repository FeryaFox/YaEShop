package ru.feryafox.productservice.feigns;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.models.internal.requests.AddToCartRequest;

@FeignClient(name = "cart-service")
public interface CartClient {
    @PostMapping("/internal/cart/{userId}")
    void addToCart(
            @PathVariable("userId") String userId,
            @RequestParam(name = "quantity", defaultValue = "1") int quantity,
            @RequestBody AddToCartRequest addToCartRequest,
            @RequestHeader("X-Internal-API-Key") String apiKey
    );
}
