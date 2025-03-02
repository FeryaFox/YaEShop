package ru.feryafox.cartservice.controllers.publics;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.cartservice.models.requests.PatchCartRequest;
import ru.feryafox.cartservice.services.CartService;

@RestController
@RequestMapping("/cart/")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("")
    public ResponseEntity<?> getCart(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        var response = cartService.getCartInfo(userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("{productId}")
    public ResponseEntity<?> updateProduct(
            @PathVariable("productId") String productId,
            @RequestBody PatchCartRequest patchCartRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        cartService.patchCart(productId, userDetails.getUsername(), patchCartRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
