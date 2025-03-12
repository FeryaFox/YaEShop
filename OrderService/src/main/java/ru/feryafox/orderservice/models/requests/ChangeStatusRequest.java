package ru.feryafox.orderservice.models.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Запрос на изменение статуса заказа")
public class ChangeStatusRequest {

    @NotNull(message = "Новый статус заказа обязателен")
    @Schema(description = "Новый статус заказа", example = "DELIVERED")
    private OrderStatus newStatus;

    public enum OrderStatus {
        PROCESSING,
        SHIPPED,
        DELIVERED,
        COMPLETED
    }
}
