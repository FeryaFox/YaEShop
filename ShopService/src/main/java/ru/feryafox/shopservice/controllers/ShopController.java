package ru.feryafox.shopservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.feryafox.shopservice.models.requests.CreateShopRequest;
import ru.feryafox.shopservice.models.responses.CreateShopResponse;
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

    @PostMapping(value = "{shopId}/upload_image", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadImage(
            @PathVariable("shopId") String shopId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws Exception {
        return ResponseEntity.ok(shopService.uploadImage(file, UUID.fromString(shopId), userDetails.getUsername()));
    }

}
