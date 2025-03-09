package ru.feryafox.orderservice.services.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.PaymentRequestEvent;
import ru.feryafox.orderservice.entities.Order;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaService {
    private final KafkaProducerService kafkaProducerService;

    public void createPaymentRequest(Order order) {
        if (order == null || order.getId() == null || order.getUserId() == null || order.getTotalPrice() == null) {
            log.error("Ошибка: Попытка создать PaymentRequestEvent с некорректными данными: {}", order);
            return;
        }

        var paymentRequest = PaymentRequestEvent.builder()
                .orderId(order.getId().toString())
                .userId(order.getUserId().toString())
                .totalPrice(order.getTotalPrice().doubleValue())
                .build();

        log.info("Создание PaymentRequestEvent для заказа {} пользователя {}. Сумма: {}",
                paymentRequest.getOrderId(), paymentRequest.getUserId(), paymentRequest.getTotalPrice());

        kafkaProducerService.sendPaymentRequestEvent(paymentRequest);
    }
}
