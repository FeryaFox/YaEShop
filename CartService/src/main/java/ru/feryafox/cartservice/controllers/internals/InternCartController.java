package ru.feryafox.cartservice.controllers.internals;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.cartservice.services.CartService;
import ru.feryafox.models.internal.requests.AddToCartRequest;

@RestController
@RequestMapping("/internal/cart/")
@RequiredArgsConstructor
public class InternCartController {
    private final CartService cartService;

    @PostMapping("{userId}")
    public ResponseEntity<?> addProduct(
            @PathVariable("userId") String userId,
            @RequestParam(name = "quantity", defaultValue = "1") int quantity,
            @RequestBody AddToCartRequest addToCartRequest
    ) {
        cartService.addToCart(userId, quantity, addToCartRequest);
        return ResponseEntity.noContent().build();
    }

}
