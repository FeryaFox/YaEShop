package ru.feryafox.orderservice.services.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.OrderEvent;
import ru.feryafox.kafka.models.PaymentResponseEvent;
import ru.feryafox.orderservice.services.OrderService;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final OrderService orderService;

    @KafkaListener(topics = "order-topic")
    public void listenCreateOrder(ConsumerRecord<String, Object> record) {
        log.info("Получено сообщение из Kafka (топик order-topic): {}", record.value());

        Object event = record.value();
        if (event instanceof OrderEvent orderEvent) {
            log.info("Обрабатываем событие OrderEvent с ID заказа: {}", orderEvent.getOrderId());
            orderService.createOrder(orderEvent);
        } else {
            log.warn("Получено некорректное сообщение в order-topic: {}", event);
        }
    }

    @KafkaListener(topics = "payment-response-topic")
    public void listenPaymentResponse(ConsumerRecord<String, Object> record) {
        log.info("Получено сообщение из Kafka (топик payment-response-topic): {}", record.value());

        Object event = record.value();
        if (event instanceof PaymentResponseEvent paymentResponseEvent) {
            log.info("Обрабатываем событие PaymentResponseEvent для заказа: {}", paymentResponseEvent.getOrderId());
            orderService.processPayment(paymentResponseEvent);
        } else {
            log.warn("Получено некорректное сообщение в payment-response-topic: {}", event);
        }
    }
}
