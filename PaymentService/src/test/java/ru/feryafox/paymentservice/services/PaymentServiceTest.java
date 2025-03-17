package ru.feryafox.paymentservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.feryafox.kafka.models.PaymentRequestEvent;
import ru.feryafox.paymentservice.entities.Payment;
import ru.feryafox.paymentservice.models.responses.AwaitingPaymentsResponse;
import ru.feryafox.paymentservice.repositories.PaymentRepository;
import ru.feryafox.paymentservice.services.kafka.KafkaService;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BaseService baseService;

    @Mock
    private KafkaService kafkaService;

    @InjectMocks
    private PaymentService paymentService;

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
    void testCreatePaymentFromEvent() {
        PaymentRequestEvent event = PaymentRequestEvent.builder()
                .orderId(payment.getOrderId())
                .userId(payment.getUserId().toString())
                .totalPrice(payment.getTotalPrice().doubleValue())
                .build();

        when(paymentRepository.save(any())).thenReturn(payment);

        paymentService.createPaymentFromEvent(event);

        verify(paymentRepository, times(1)).save(any());
    }

    @Test
    void testGetAwaitingPayments() {
        when(baseService.getNotPaidPayments(payment.getUserId())).thenReturn(Set.of(payment));

        AwaitingPaymentsResponse response = paymentService.getAwaitingPayments(payment.getUserId().toString());

        assertNotNull(response);
        assertEquals(1, response.getAwaitingPayments().size());
    }

    @Test
    void testProcessPayment() {
        when(baseService.getPaymentById(payment.getId())).thenReturn(payment);
        when(paymentRepository.save(any())).thenReturn(payment);
        doNothing().when(kafkaService).sendResponse(any());

        paymentService.processPayment(payment.getId().toString());

        assertEquals(Payment.PaymentStatus.PAID, payment.getPaymentStatus());
        verify(paymentRepository, times(1)).save(any());
        verify(kafkaService, times(1)).sendResponse(any());
    }
}
