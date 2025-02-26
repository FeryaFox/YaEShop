package ru.feryafox.shopservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.feryafox.models.internal.responses.ShopInfoInternalResponse;
import ru.feryafox.shopservice.models.responses.ShopInfoResponse;
import ru.feryafox.shopservice.services.ShopService;

import java.util.UUID;

@RestController
@RequestMapping("/internal/shop/")
@RequiredArgsConstructor
public class ShopInternalController {
    private final ShopService shopService;

    @GetMapping("{shopId}")
    public ResponseEntity<?> getShopInfo(
            @PathVariable("shopId") String shopId
    ) {
        ShopInfoInternalResponse response = shopService.getIternalShopInfo(UUID.fromString(shopId));

        return ResponseEntity.ok().body(response);
    }
}
