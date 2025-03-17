package ru.feryafox.productservice.controllers.publics.authrequired;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.feryafox.productservice.entities.mongo.Product;
import ru.feryafox.productservice.models.requests.CreateProductRequest;
import ru.feryafox.productservice.models.requests.UpdateProductRequest;
import ru.feryafox.productservice.models.responses.CreateProductResponse;
import ru.feryafox.productservice.models.responses.UploadImageResponse;
import ru.feryafox.productservice.services.ProductService;

@RestController
@RequestMapping("/product/")
@RequiredArgsConstructor
@Tag(name = "ProductController", description = "Управление продуктами")
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "Добавить новый продукт", description = "Создает новый продукт и связывает его с магазином.")
    @PostMapping("")
    public ResponseEntity<?> addProduct(
            @RequestBody CreateProductRequest createProductRequest,
            @AuthenticationPrincipal
            @Parameter(description = "Детали аутентифицированного пользователя")
            UserDetails userDetails
    ) {
        CreateProductResponse createProductResponse = productService.createProduct(createProductRequest, userDetails.getUsername());
        return ResponseEntity.ok(createProductResponse);
    }

    @Operation(summary = "Загрузить изображение продукта", description = "Загружает изображение для указанного продукта.")
    @PostMapping("{productId}/upload_image")
    public ResponseEntity<?> uploadImage(
            @PathVariable("productId")
            @Parameter(description = "Идентификатор продукта")
            String productId,
            @RequestParam("file")
            @Parameter(description = "Файл изображения")
            MultipartFile file,
            @AuthenticationPrincipal
            @Parameter(description = "Детали аутентифицированного пользователя")
            UserDetails userDetails
    ) throws Exception {
        UploadImageResponse response = productService.uploadImage(file, productId, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Обновить информацию о продукте", description = "Обновляет параметры продукта по его идентификатору.")
    @PutMapping("{productId}")
    public ResponseEntity<?> updateProduct(
            @PathVariable("productId")
            @Parameter(description = "Идентификатор продукта")
            String productId,
            @RequestBody
            @Parameter(description = "Обновленные данные продукта")
            UpdateProductRequest updateProductRequest,
            @AuthenticationPrincipal
            @Parameter(description = "Детали аутентифицированного пользователя")
            UserDetails userDetails
    ) {
        Product updatedProduct = productService.updateProduct(productId, updateProductRequest, userDetails.getUsername());
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "Добавить продукт в корзину", description = "Добавляет указанный продукт в корзину пользователя.")
    @PostMapping("{productId}/add_to_cart")
    public ResponseEntity<?> addToCart(
            @PathVariable("productId")
            @Parameter(description = "Идентификатор продукта")
            String productId,
            @RequestParam(name = "quantity", defaultValue = "1")
            @Parameter(description = "Количество товара")
            int quantity,
            @AuthenticationPrincipal
            @Parameter(description = "Детали аутентифицированного пользователя")
            UserDetails userDetails
    ) {
        productService.addToCart(productId, quantity, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
