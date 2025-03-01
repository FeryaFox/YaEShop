package ru.feryafox.shopservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.models.internal.responses.ProductInfoInternResponse;
import ru.feryafox.shopservice.models.responses.ShopInfoResponse;
import ru.feryafox.shopservice.services.ProductsService;
import ru.feryafox.shopservice.services.ShopService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shop/")
@RequiredArgsConstructor
public class ShopPublicController {
    private final ShopService shopService;
    private final ProductsService productsService;

    @GetMapping("{shopId}")
    public ResponseEntity<?> getShopInfo(
            @PathVariable("shopId") String shopId
    ) {
        ShopInfoResponse response = shopService.getShopInfo(UUID.fromString(shopId));

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("{shopId}/products")
    public ResponseEntity<?> getShopProducts(
            @PathVariable("shopId") String shopId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        var responses = productsService.getInternProducts(shopId, page, size);
        return ResponseEntity.ok().body(responses);
    }
}
