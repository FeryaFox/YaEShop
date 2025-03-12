package ru.feryafox.orderservice.models.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.feryafox.orderservice.entities.Order;
import ru.feryafox.orderservice.entities.ProductItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Ответ с информацией о заказах пользователя")
public class UserOrderInfoResponse {

    @Schema(description = "Список заказов пользователя")
    private Set<OrderInfo> orders;

    public static UserOrderInfoResponse fromOrders(List<Order> orders) {
        var orderInfos = orders.stream()
                .map(OrderInfo::from)
                .collect(Collectors.toSet());

        return new UserOrderInfoResponse(orderInfos);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "Информация о заказе пользователя")
    public static class OrderInfo {

        @Schema(description = "Идентификатор заказа", example = "550e8400-e29b-41d4-a716-446655440000")
        private String orderId;

        @Schema(description = "Статус заказа", example = "DELIVERED")
        private Order.OrderStatus status;

        @Schema(description = "Дата и время создания заказа", example = "2024-03-12T15:30:00")
        private LocalDateTime orderTime;

        @Schema(description = "Общая стоимость заказа", example = "1999.99")
        private double totalPrice;

        @Schema(description = "Список товаров в заказе")
        private Set<ProductInfo> productItems;

        public static OrderInfo from(Order order) {
            return OrderInfo.builder()
                    .orderId(order.getId().toString())
                    .status(order.getOrderStatus())
                    .orderTime(order.getCreatedAt())
                    .totalPrice(order.getTotalPrice().doubleValue())
                    .productItems(order.getProductItems().stream()
                            .map(ProductInfo::from)
                            .collect(Collectors.toSet()))
                    .build();
        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        @Schema(description = "Информация о товаре в заказе")
        public static class ProductInfo {

            @Schema(description = "Идентификатор товара", example = "abc123")
            private String productId;

            @Schema(description = "Количество товара", example = "2")
            private int quantity;

            @Schema(description = "Цена за единицу товара", example = "999.99")
            private double price;

            public static ProductInfo from(ProductItem product) {
                return ProductInfo.builder()
                        .productId(product.getProductId())
                        .quantity(product.getQuantity())
                        .price(product.getPrice().doubleValue())
                        .build();
            }
        }
    }
}
