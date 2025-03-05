package ru.feryafox.orderservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.feryafox.orderservice.services.OrderService;

@RestController
@RequestMapping("/order/buyer/")
@RequiredArgsConstructor
public class OrderBuyerController {
    private final OrderService orderService;

    @GetMapping("all_orders")
    public ResponseEntity<?> getOrderBuyer(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        var responses = orderService.getUserOrderInfo(userDetails.getUsername());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("finished")
    public ResponseEntity<?> get(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        var responses = orderService.getFinishedUserOrderInfo(userDetails.getUsername());
        return ResponseEntity.ok(responses);
    }
}
