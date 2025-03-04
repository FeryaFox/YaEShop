package ru.feryafox.paymentservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.feryafox.paymentservice.entities.Payment;

import java.util.Set;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Set<Payment> findByUserIdAndPaymentStatus(UUID userId, Payment.PaymentStatus paymentStatus);
}