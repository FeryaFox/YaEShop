package ru.feryafox.kafka.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductEvent implements BaseKafkaModel{
    private final String type = "ProductEvent";

    private String productId;
    private String shopId;
    private String ownerId;
    private String name;
    private ShopStatus status;

    public enum ShopStatus {
        CREATED,
        UPDATED,
        DELETED
    }
}
