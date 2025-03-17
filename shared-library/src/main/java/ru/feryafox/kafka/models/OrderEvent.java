package ru.feryafox.kafka.models;

import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEvent implements BaseKafkaModel{
    private final String type = "OrderEvent";

    private String orderId;
    private String userId;
    private Set<ProductItem> productItems;
    private OrderStatus orderStatus;
    private double totalPrice;

    @Override
    public String getType() {
        return type;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProductItem {
        private String productId;
        private int quantity;
        private double price;
        private String shopId;
    }

    public enum OrderStatus {
        CREATED,
        UPDATED,
        DELETED
    }
}
