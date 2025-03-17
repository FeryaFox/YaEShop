package ru.feryafox.productservice.controllers.publics.publics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.productservice.models.responses.ProductInfoResponse;
import ru.feryafox.productservice.services.ProductService;

@RestController
@RequestMapping("/product/")
@RequiredArgsConstructor
@Tag(name = "ProductPublicController", description = "Публичный доступ к информации о продуктах")
public class ProductPublicController {
    private final ProductService productService;

    @Operation(summary = "Получить информацию о продукте", description = "Возвращает подробную информацию о продукте по его ID.")
    @GetMapping("{productId}")
    public ResponseEntity<?> getProduct(
            @PathVariable("productId")
            @Parameter(description = "Идентификатор продукта", required = true)
            String productId
    ) {
        ProductInfoResponse productInfoResponse = productService.getProductInfo(productId);
        return ResponseEntity.ok(productInfoResponse);
    }

    @Operation(summary = "Получить список продуктов магазина", description = "Возвращает список всех продуктов, принадлежащих указанному магазину.")
    @GetMapping("shop/{shopId}")
    public ResponseEntity<?> getStore(
            @PathVariable("shopId")
            @Parameter(description = "Идентификатор магазина", required = true)
            String shopId,
            @RequestParam(name = "page", defaultValue = "0")
            @Parameter(description = "Номер страницы для пагинации", example = "0")
            int page,
            @RequestParam(name = "size", defaultValue = "10")
            @Parameter(description = "Размер страницы для пагинации", example = "10")
            int size
    ) {
        var response = productService.getProductInfoFromShop(shopId, page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Поиск продуктов по имени", description = "Ищет продукты по части названия, с поддержкой пагинации.")
    @GetMapping("search")
    public ResponseEntity<?> searchProductsByName(
            @RequestParam("name")
            @Parameter(description = "Название продукта или его часть", required = true)
            String name,
            @RequestParam(name = "page", defaultValue = "0")
            @Parameter(description = "Номер страницы для пагинации", example = "0")
            int page,
            @RequestParam(name = "size", defaultValue = "10")
            @Parameter(description = "Размер страницы для пагинации", example = "10")
            int size
    ) {
        var response = productService.searchProductsByName(name, page, size);
        return ResponseEntity.ok(response);
    }
}
