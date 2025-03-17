package ru.feryafox.orderservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.feryafox.orderservice.models.responses.UserOrderInfoResponse;
import ru.feryafox.orderservice.services.OrderService;

@RestController
@RequestMapping("/order/buyer/")
@RequiredArgsConstructor
@Tag(name = "Order Buyer", description = "API для управления заказами покупателей")
public class OrderBuyerController {
    private final OrderService orderService;

    @Operation(summary = "Получить список активных заказов покупателя", responses = {
            @ApiResponse(responseCode = "200", description = "Список активных заказов успешно получен",
                    content = @Content(schema = @Schema(implementation = UserOrderInfoResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content)
    })
    @GetMapping("all_orders")
    public ResponseEntity<?> getOrderBuyer(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        var responses = orderService.getUserOrderInfo(userDetails.getUsername());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Получить список завершенных заказов покупателя", responses = {
            @ApiResponse(responseCode = "200", description = "Список завершенных заказов успешно получен",
                    content = @Content(schema = @Schema(implementation = UserOrderInfoResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content)
    })
    @GetMapping("finished")
    public ResponseEntity<?> get(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        var responses = orderService.getFinishedUserOrderInfo(userDetails.getUsername());
        return ResponseEntity.ok(responses);
    }
}
