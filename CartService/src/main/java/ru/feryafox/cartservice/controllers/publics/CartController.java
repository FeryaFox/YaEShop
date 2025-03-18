package ru.feryafox.cartservice.controllers.publics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.cartservice.models.requests.PatchCartRequest;
import ru.feryafox.cartservice.models.responses.CartInfoResponse;
import ru.feryafox.cartservice.services.CartService;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "API для управления корзиной покупателя")
public class CartController {
    private final CartService cartService;

    @Operation(summary = "Получить содержимое корзины", responses = {
            @ApiResponse(responseCode = "200", description = "Корзина успешно загружена",
                    content = @Content(schema = @Schema(implementation = CartInfoResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content)
    })
    @GetMapping("/")
    public ResponseEntity<CartInfoResponse> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.getCartInfo(userDetails.getUsername()));
    }

    @Operation(summary = "Обновить количество товара в корзине", responses = {
            @ApiResponse(responseCode = "204", description = "Количество товара успешно обновлено"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content)
    })
    @PatchMapping("/{productId}")
    public ResponseEntity<Void> updateProduct(
            @PathVariable(name = "productId") String productId,
            @RequestBody PatchCartRequest patchCartRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        cartService.patchCart(productId, userDetails.getUsername(), patchCartRequest);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удалить товар из корзины", responses = {
            @ApiResponse(responseCode = "204", description = "Товар успешно удалён из корзины"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content)
    })
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable(name = "productId") String productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        cartService.deleteProductFromCart(productId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Очистить корзину", responses = {
            @ApiResponse(responseCode = "204", description = "Корзина успешно очищена"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content)
    })
    @DeleteMapping("/")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCart(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Оформить заказ", responses = {
            @ApiResponse(responseCode = "204", description = "Заказ успешно создан"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content)
    })
    @PostMapping("/create_order")
    public ResponseEntity<Void> createOrder(@AuthenticationPrincipal UserDetails userDetails) {
        cartService.createOrder(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
