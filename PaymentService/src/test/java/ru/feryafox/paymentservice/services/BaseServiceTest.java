package ru.feryafox.paymentservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.feryafox.paymentservice.entities.Payment;
import ru.feryafox.paymentservice.exceptions.PaymentIsNotExistsException;
import ru.feryafox.paymentservice.repositories.PaymentRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private BaseService baseService;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder()
                .id(UUID.randomUUID())
                .orderId(UUID.randomUUID().toString())
                .userId(UUID.randomUUID())
                .totalPrice(BigDecimal.valueOf(200))
                .paymentStatus(Payment.PaymentStatus.NOT_PAID)
                .build();
    }

    @Test
    void testGetNotPaidPayments() {
        when(paymentRepository.findByUserIdAndPaymentStatus(payment.getUserId(), Payment.PaymentStatus.NOT_PAID))
                .thenReturn(Set.of(payment));

        Set<Payment> payments = baseService.getNotPaidPayments(payment.getUserId());

        assertNotNull(payments);
        assertEquals(1, payments.size());
    }

    @Test
    void testGetPaymentById_Exists() {
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));

        Payment foundPayment = baseService.getPaymentById(payment.getId());

        assertNotNull(foundPayment);
        assertEquals(payment.getId(), foundPayment.getId());
    }

    @Test
    void testGetPaymentById_NotExists() {
        UUID randomId = UUID.randomUUID();
        when(paymentRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(PaymentIsNotExistsException.class, () -> baseService.getPaymentById(randomId));
    }
}
