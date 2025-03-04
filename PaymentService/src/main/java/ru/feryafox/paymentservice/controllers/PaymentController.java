package ru.feryafox.paymentservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.paymentservice.services.PaymentService;

@RestController
@RequestMapping("/payment/")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("awaiting_payment")
    public ResponseEntity<?> awaitingPayment(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        var response = paymentService.getAwaitingPayments(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("pay/{paymentId}")
    public ResponseEntity<?> processPayment(
            @PathVariable(name = "paymentId") String paymentId
    ) {
        paymentService.processPayment(paymentId);
        return ResponseEntity.noContent().build();
    }
}
