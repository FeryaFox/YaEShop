package ru.feryafox.shopservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.feryafox.shopservice.models.responses.ShopInfoResponse;
import ru.feryafox.shopservice.services.ShopService;

import java.util.UUID;

@RestController
@RequestMapping("/shop/")
@RequiredArgsConstructor
public class ShopPublicController {
    private final ShopService shopService;

    @GetMapping("{shopId}")
    public ResponseEntity<?> getShopInfo(
            @PathVariable("shopId") String shopId
    ) {
        ShopInfoResponse response = shopService.getShopInfo(UUID.fromString(shopId));

        return ResponseEntity.ok().body(response);
    }
}
