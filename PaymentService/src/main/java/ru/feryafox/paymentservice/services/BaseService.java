package ru.feryafox.paymentservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.feryafox.paymentservice.entities.Payment;
import ru.feryafox.paymentservice.exceptions.PaymentIsNotExistsException;
import ru.feryafox.paymentservice.repositories.PaymentRepository;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BaseService {
    private final PaymentRepository paymentRepository;

    public Set<Payment> getNotPaidPayments(UUID userId) {
        log.info("Запрос неоплаченных платежей для пользователя: {}", userId);

        Set<Payment> payments = paymentRepository.findByUserIdAndPaymentStatus(userId, Payment.PaymentStatus.NOT_PAID);

        if (payments.isEmpty()) {
            log.warn("Для пользователя {} нет неоплаченных платежей", userId);
        } else {
            log.info("Найдено {} неоплаченных платежей для пользователя {}", payments.size(), userId);
        }

        return payments;
    }

    public Payment getPaymentById(UUID paymentId) {
        log.info("Поиск платежа по ID: {}", paymentId);

        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    log.warn("Платеж с ID {} не найден", paymentId);
                    return new PaymentIsNotExistsException(paymentId.toString());
                });
    }
}
