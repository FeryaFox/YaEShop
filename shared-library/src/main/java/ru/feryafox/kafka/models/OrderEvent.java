package ru.feryafox.kafka.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProductItem {
        private String productId;
        private int quantity;
    }

    public enum OrderStatus {
        CREATED,
        UPDATED,
        DELETED
    }
}
