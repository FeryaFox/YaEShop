package ru.feryafox.paymentservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feryafox.paymentservice.entities.Payment;
import ru.feryafox.paymentservice.exceptions.PaymentIsNotExistsException;
import ru.feryafox.paymentservice.repositories.PaymentRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BaseService {
    private final PaymentRepository paymentRepository;

    public Set<Payment> getNotPaidPayments(UUID userId) {
       return paymentRepository.findByUserIdAndPaymentStatus(userId, Payment.PaymentStatus.NOT_PAID);
    }

    public Payment getPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(() -> new PaymentIsNotExistsException(paymentId.toString()));
    }
}
