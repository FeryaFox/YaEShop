package ru.feryafox.cartservice.controllers.publics;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
        return ResponseEntity.ok(response);
    }
}
