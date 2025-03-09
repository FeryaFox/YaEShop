package ru.feryafox.paymentservice.services.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.PaymentResponseEvent;
import ru.feryafox.paymentservice.entities.Payment;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaService {
    private final KafkaProducerService kafkaProducerService;

    public void sendResponse(Payment payment) {
        if (payment == null || payment.getId() == null || payment.getOrderId() == null
                || payment.getUserId() == null || payment.getTotalPrice() == null) {
            log.error("Ошибка: Попытка отправить PaymentResponseEvent с некорректными данными: {}", payment);
            return;
        }

        var paymentResponseEvent = PaymentResponseEvent.builder()
                .paymentId(payment.getId().toString())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId().toString())
                .totalPrice(payment.getTotalPrice().doubleValue())
                .build();

        log.info("Создание PaymentResponseEvent для платежа {} заказа {} пользователя {}. Сумма: {}",
                paymentResponseEvent.getPaymentId(), paymentResponseEvent.getOrderId(),
                paymentResponseEvent.getUserId(), paymentResponseEvent.getTotalPrice());

        kafkaProducerService.send(paymentResponseEvent);
    }
}
