package ru.feryafox.shopservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "ShopPublicController", description = "Публичный API для получения информации о магазинах и их товарах")
public class ShopPublicController {
    private final ShopService shopService;
    private final ProductsService productsService;

    @Operation(summary = "Получить информацию о магазине", description = "Возвращает данные о магазине по его идентификатору")
    @GetMapping("{shopId}")
    public ResponseEntity<ShopInfoResponse> getShopInfo(
            @PathVariable("shopId")
            @Parameter(description = "Идентификатор магазина", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            String shopId
    ) {
        ShopInfoResponse response = shopService.getShopInfo(UUID.fromString(shopId));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получить список товаров магазина", description = "Возвращает товары магазина с поддержкой пагинации")
    @GetMapping("{shopId}/products")
    public ResponseEntity<List<ProductInfoInternResponse>> getShopProducts(
            @PathVariable("shopId")
            @Parameter(description = "Идентификатор магазина", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            String shopId,
            @RequestParam(name = "page", defaultValue = "0")
            @Parameter(description = "Номер страницы (начиная с 0)", example = "0")
            int page,
            @RequestParam(name = "size", defaultValue = "10")
            @Parameter(description = "Количество элементов на странице", example = "10")
            int size
    ) {
        List<ProductInfoInternResponse> responses = productsService.getInternProducts(shopId, page, size);
        return ResponseEntity.ok(responses);
    }
}
