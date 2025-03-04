package ru.feryafox.paymentservice.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.feryafox.paymentservice.entities.Payment;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AwaitingPaymentsResponse {
    private Set<AwaitingPayment> awaitingPayments;

    public static AwaitingPaymentsResponse from(Set<Payment> awaitingPayments) {
        var payments = awaitingPayments.stream().map(AwaitingPayment::from).collect(Collectors.toSet());
        return new AwaitingPaymentsResponse(payments);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AwaitingPayment {
       private String paymentId;
       private String orderId;
       private double totalPrice;
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
