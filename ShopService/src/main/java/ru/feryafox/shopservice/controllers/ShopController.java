package ru.feryafox.shopservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.feryafox.shopservice.models.requests.CreateShopRequest;
import ru.feryafox.shopservice.models.requests.UpdateShopRequest;
import ru.feryafox.shopservice.models.responses.CreateShopResponse;
import ru.feryafox.shopservice.models.responses.UploadImageResponse;
import ru.feryafox.shopservice.services.ShopService;

import java.util.UUID;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
@Tag(name = "ShopController", description = "Управление магазинами (создание, обновление, загрузка изображений)")
public class ShopController {
    private final ShopService shopService;

    @Operation(summary = "Создать магазин", description = "Позволяет продавцу создать новый магазин")
    @PostMapping
    public ResponseEntity<CreateShopResponse> createShop(
            @RequestBody CreateShopRequest createShopRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(shopService.createShop(createShopRequest, userDetails.getUsername()));
    }

    @Operation(summary = "Загрузить изображение для магазина", description = "Позволяет загрузить изображение магазина")
    @PostMapping(value = "/{shopId}/upload_image", consumes = "multipart/form-data")
    public ResponseEntity<UploadImageResponse> uploadImage(
            @PathVariable @Parameter(description = "Идентификатор магазина", required = true) UUID shopId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws Exception {
        return ResponseEntity.ok(shopService.uploadImage(file, shopId, userDetails.getUsername()));
    }

    @Operation(summary = "Обновить информацию о магазине", description = "Позволяет обновить название и описание магазина")
    @PutMapping("/{shopId}")
    public ResponseEntity<Void> updateShop(
            @RequestBody UpdateShopRequest updateShopRequest,
            @PathVariable @Parameter(description = "Идентификатор магазина", required = true) UUID shopId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        shopService.updateShop(updateShopRequest, shopId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
