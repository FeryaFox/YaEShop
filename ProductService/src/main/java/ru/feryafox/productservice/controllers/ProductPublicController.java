package ru.feryafox.productservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.feryafox.productservice.entities.mongo.Product;
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
}
