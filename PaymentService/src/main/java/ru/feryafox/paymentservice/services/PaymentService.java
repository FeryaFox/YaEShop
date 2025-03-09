package ru.feryafox.paymentservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.feryafox.kafka.models.PaymentRequestEvent;
import ru.feryafox.paymentservice.entities.Payment;
import ru.feryafox.paymentservice.models.responses.AwaitingPaymentsResponse;
import ru.feryafox.paymentservice.repositories.PaymentRepository;
import ru.feryafox.paymentservice.services.kafka.KafkaService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BaseService baseService;
    private final KafkaService kafkaService;

    @Transactional
    public void createPaymentFromEvent(PaymentRequestEvent paymentRequestEvent) {
        log.info("Создание нового платежа на основе события PaymentRequestEvent: {}", paymentRequestEvent);

        var payment = Payment.from(paymentRequestEvent);
        paymentRepository.save(payment);

        log.info("Платеж {} успешно создан для пользователя {}", payment.getId(), payment.getUserId());
    }

    public AwaitingPaymentsResponse getAwaitingPayments(String userId) {
        log.info("Запрос ожидающих платежей для пользователя {}", userId);

        UUID userUuid;
        try {
            userUuid = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            log.error("Ошибка: Некорректный UUID пользователя: {}", userId, e);
            return AwaitingPaymentsResponse.empty();
        }

        var awaitingPayments = baseService.getNotPaidPayments(userUuid);
        log.info("Найдено {} ожидающих платежей для пользователя {}", awaitingPayments.size(), userId);

        return AwaitingPaymentsResponse.from(awaitingPayments);
    }

    @Transactional
    public void processPayment(String paymentId) {
        log.info("Обработка платежа с ID {}", paymentId);

        UUID paymentUuid;
        try {
            paymentUuid = UUID.fromString(paymentId);
        } catch (IllegalArgumentException e) {
            log.error("Ошибка: Некорректный UUID платежа: {}", paymentId, e);
            return;
        }

        var payment = baseService.getPaymentById(paymentUuid);
        payment.setPaymentStatus(Payment.PaymentStatus.PAID);
        payment = paymentRepository.save(payment);

        log.info("Платеж {} успешно обработан и обновлен в базе данных", paymentId);

        try {
            kafkaService.sendResponse(payment);
            log.info("Успешно отправлено событие об оплате в Kafka для платежа {}", paymentId);
        } catch (Exception e) {
            log.error("Ошибка при отправке события об оплате в Kafka для платежа {}: {}", paymentId, e.getMessage(), e);
        }
    }
}
