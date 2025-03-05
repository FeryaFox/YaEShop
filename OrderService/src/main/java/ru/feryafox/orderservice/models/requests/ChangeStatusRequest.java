package ru.feryafox.orderservice.models.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeStatusRequest {
    private OrderStatus newStatus;

    public enum OrderStatus {
        PROCESSING,
        SHIPPED,
        DELIVERED,
        COMPLETED
    }
}
