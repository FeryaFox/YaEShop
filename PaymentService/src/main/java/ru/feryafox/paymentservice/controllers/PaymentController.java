package ru.feryafox.paymentservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
