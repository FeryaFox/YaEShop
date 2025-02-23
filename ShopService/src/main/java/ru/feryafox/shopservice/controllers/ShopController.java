package ru.feryafox.shopservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.shopservice.entitis.Shop;
import ru.feryafox.shopservice.models.requests.CreateShopRequest;
import ru.feryafox.shopservice.models.responses.CreateShopResponse;
import ru.feryafox.shopservice.models.responses.ShopInfoResponse;
import ru.feryafox.shopservice.services.BaseService;
import ru.feryafox.shopservice.services.ShopService;

import java.util.UUID;

@RestController
@RequestMapping("/shop/")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;

    @PostMapping("")
    public ResponseEntity<?> createShop(
            @RequestBody CreateShopRequest createShopRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CreateShopResponse response = shopService.createShop(createShopRequest, userDetails.getUsername());
        return ResponseEntity.ok().body(response);
    }

}
