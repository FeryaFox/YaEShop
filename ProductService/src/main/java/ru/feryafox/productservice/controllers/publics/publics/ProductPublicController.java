package ru.feryafox.productservice.controllers.publics.publics;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.productservice.models.responses.ProductInfoResponse;
import ru.feryafox.productservice.services.ProductService;

@RestController
@RequestMapping("/product/")
@RequiredArgsConstructor
public class ProductPublicController {
    private final ProductService productService;

    @GetMapping("{productId}")
    public ResponseEntity<?> getProduct(@PathVariable("productId") String productId) {
        ProductInfoResponse productInfoResponse = productService.getProductInfo(productId);

        return ResponseEntity.ok(productInfoResponse);
    }

    @GetMapping("shop/{shopId}")
    public ResponseEntity<?> getStore(
            @PathVariable("shopId") String shopId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        var response = productService.getProductInfoFromShop(shopId, page, size);
        return ResponseEntity.ok(response);
    }
}
