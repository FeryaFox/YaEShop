package ru.feryafox.productservice.controllers;

import jakarta.ws.rs.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.feryafox.productservice.entities.mongo.Product;
import ru.feryafox.productservice.models.requests.CreateProductRequest;
import ru.feryafox.productservice.models.responses.CreateProductResponse;
import ru.feryafox.productservice.models.responses.UploadImageResponse;
import ru.feryafox.productservice.services.ProductService;

import java.util.UUID;

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

    @PostMapping("{productId}/upload_image")
    public ResponseEntity<?> uploadImage(
        @PathVariable("productId") String productId,
        @RequestParam("file") MultipartFile file,
        @AuthenticationPrincipal UserDetails userDetails
    ) throws Exception {
        UploadImageResponse response = productService.uploadImage(file, productId, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}
