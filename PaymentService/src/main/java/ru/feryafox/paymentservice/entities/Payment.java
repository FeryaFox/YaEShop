package ru.feryafox.paymentservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import ru.feryafox.kafka.models.PaymentRequestEvent;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {
    @Id
    @Column(nullable = false, unique = true, updatable = false)
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    public enum PaymentStatus {
        NOT_PAID,
        PAID
    }

    public static Payment from(PaymentRequestEvent paymentRequestEvent) {
         return Payment.builder()
                .orderId(paymentRequestEvent.getOrderId())
                .userId(UUID.fromString(paymentRequestEvent.getUserId()))
                .totalPrice(BigDecimal.valueOf(paymentRequestEvent.getTotalPrice()))
                .paymentStatus(Payment.PaymentStatus.NOT_PAID)
                .build();
    }

}
