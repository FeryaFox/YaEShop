package ru.feryafox.orderservice.models.responses;

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
public class UserOrderInfoResponse {

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
    public static class OrderInfo {
        private String orderId;
        private Order.OrderStatus status;
        private LocalDateTime orderTime;
        private double totalPrice;
        private Set<ProductInfo> productItems;

        public static OrderInfo from(Order order) {
            return OrderInfo.builder()
                    .orderId(order.getId().toString())
                    .status(order.getOrderStatus())
                    .orderTime(order.getCreatedAt())
                    .totalPrice(order.getTotalPrice().doubleValue())
                    .productItems(order.getProductItems().stream()
                            .map(ProductInfo::from)
                            .collect(Collectors.toSet())
                    )
                    .build();
        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        public static class ProductInfo {
            private String productId;
            private int quantity;
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

