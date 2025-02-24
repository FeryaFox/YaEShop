package ru.feryafox.productservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.feryafox.productservice.entities.mongo.Product;
import ru.feryafox.productservice.models.requests.CreateProductRequest;
import ru.feryafox.productservice.models.responses.CreateProductResponse;
import ru.feryafox.productservice.services.ProductService;

@RestController
@RequestMapping("/product/")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;


    @PostMapping("")
    public ResponseEntity<?> addProduct(
            @RequestBody CreateProductRequest createProductRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CreateProductResponse createProductResponse = productService.createProduct(createProductRequest, userDetails.getUsername());
        return ResponseEntity.ok(createProductResponse);
    }
}
