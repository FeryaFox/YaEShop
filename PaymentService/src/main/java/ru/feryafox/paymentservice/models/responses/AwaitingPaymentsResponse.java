package ru.feryafox.paymentservice.models.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.feryafox.paymentservice.entities.Payment;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Ответ с ожидающими платежами")
public class AwaitingPaymentsResponse {

    @Schema(description = "Список ожидающих платежей")
    private Set<AwaitingPayment> awaitingPayments;

    public static AwaitingPaymentsResponse from(Set<Payment> awaitingPayments) {
        var payments = awaitingPayments.stream().map(AwaitingPayment::from).collect(Collectors.toSet());
        return new AwaitingPaymentsResponse(payments);
    }

    public static AwaitingPaymentsResponse empty() {
        return new AwaitingPaymentsResponse(Collections.emptySet());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "Информация об ожидающем платеже")
    public static class AwaitingPayment {

        @NotBlank(message = "Идентификатор платежа обязателен")
        @Schema(description = "Идентификатор платежа", example = "550e8400-e29b-41d4-a716-446655440000")
        private String paymentId;

        @NotBlank(message = "Идентификатор заказа обязателен")
        @Schema(description = "Идентификатор заказа", example = "ORD123456")
        private String orderId;

        @NotNull(message = "Сумма платежа обязательна")
        @Min(value = 0, message = "Сумма платежа не может быть отрицательной")
        @Schema(description = "Сумма платежа", example = "1500.00")
        private double totalPrice;

        @NotBlank(message = "Ссылка на оплату обязательна")
        @Schema(description = "Ссылка для оплаты платежа", example = "http://127.0.0.1:8080/payment/pay/550e8400-e29b-41d4-a716-446655440000")
        private String paymentLink;

        public static AwaitingPayment from(Payment payment) {
            return AwaitingPayment.builder()
                    .paymentId(String.valueOf(payment.getId()))
                    .orderId(payment.getOrderId())
                    .totalPrice(payment.getTotalPrice().doubleValue())
                    .paymentLink(generatePaymentLink(String.valueOf(payment.getId())))
                    .build();
        }

        private static String generatePaymentLink(String paymentId) {
            return "http://127.0.0.1:8080/payment/pay/" + paymentId;
        }
    }
}
