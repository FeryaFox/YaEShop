package ru.feryafox.orderservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.orderservice.models.requests.ChangeStatusRequest;
import ru.feryafox.orderservice.services.OrderService;

@RestController
@RequestMapping("/order/delivery/")
@RequiredArgsConstructor
@Tag(name = "Order Delivery", description = "API для управления статусами заказов на доставку")
public class OrderDeliveryController {
    private final OrderService orderService;

    @Operation(summary = "Обновить статус заказа на доставку", responses = {
            @ApiResponse(responseCode = "204", description = "Статус заказа успешно обновлен", content = @Content),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content),
            @ApiResponse(responseCode = "403", description = "У пользователя нет прав для изменения статуса", content = @Content),
            @ApiResponse(responseCode = "404", description = "Заказ не найден", content = @Content)
    })
    @PatchMapping("{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable("orderId") String orderId,
            @RequestBody ChangeStatusRequest changeStatusRequest
    ) {
        orderService.changeOrderStatus(orderId, changeStatusRequest);
        return ResponseEntity.noContent().build();
    }
}
