package ru.feryafox.orderservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.orderservice.models.requests.ChangeStatusRequest;
import ru.feryafox.orderservice.services.OrderService;

@RestController
@RequestMapping("/order/delivery/")
@RequiredArgsConstructor
public class OrderDeliveryController {
    private final OrderService orderService;

    @PatchMapping("{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable("orderId") String orderId,
            @RequestBody ChangeStatusRequest changeStatusRequest
    ) {
        orderService.changeOrderStatus(orderId, changeStatusRequest);
        return ResponseEntity.noContent().build();
    }
}
