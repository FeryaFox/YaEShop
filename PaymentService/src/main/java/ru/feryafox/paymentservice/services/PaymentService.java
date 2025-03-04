package ru.feryafox.paymentservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.feryafox.kafka.models.PaymentRequestEvent;
import ru.feryafox.paymentservice.entities.Payment;
import ru.feryafox.paymentservice.models.responses.AwaitingPaymentsResponse;
import ru.feryafox.paymentservice.repositories.PaymentRepository;
import ru.feryafox.paymentservice.services.kafka.KafkaService;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BaseService baseService;
    private final KafkaService kafkaService;

    @Transactional
    public void createPaymentFromEvent(PaymentRequestEvent paymentRequestEvent) {
        var payment = Payment.from(paymentRequestEvent);
        paymentRepository.save(payment);
    }

    public AwaitingPaymentsResponse getAwaitingPayments(String userId) {
        var awaitingPayments = baseService.getNotPaidPayments(UUID.fromString(userId));
        return AwaitingPaymentsResponse.from(awaitingPayments);
    }

    @Transactional
    public void processPayment(String paymentId) {
       var payment = baseService.getPaymentById(UUID.fromString(paymentId));

       payment.setPaymentStatus(Payment.PaymentStatus.PAID);
       payment = paymentRepository.save(payment);

       kafkaService.sendResponse(payment);
    }
}
