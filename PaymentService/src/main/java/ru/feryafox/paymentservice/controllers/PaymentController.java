package ru.feryafox.paymentservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.paymentservice.services.PaymentService;

@RestController
@RequestMapping("/payment/")
@RequiredArgsConstructor
@Tag(name = "PaymentController", description = "Управление платежами")
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "Получить ожидающие платежи", description = "Возвращает список всех неоплаченных платежей для авторизованного пользователя.")
    @GetMapping("awaiting_payment")
    public ResponseEntity<?> awaitingPayment(
            @AuthenticationPrincipal
            @Parameter(description = "Детали аутентифицированного пользователя")
            UserDetails userDetails
    ) {
        var response = paymentService.getAwaitingPayments(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Оплатить заказ", description = "Помечает платеж как оплаченный и отправляет событие в Kafka.")
    @GetMapping("pay/{paymentId}")
    public ResponseEntity<?> processPayment(
            @PathVariable(name = "paymentId")
            @Parameter(description = "Идентификатор платежа")
            String paymentId
    ) {
        paymentService.processPayment(paymentId);
        return ResponseEntity.noContent().build();
    }
}
