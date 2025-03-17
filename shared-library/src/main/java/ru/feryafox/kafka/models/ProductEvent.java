package ru.feryafox.kafka.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
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
    private ProductStatus status;
    private double price;

    public enum ProductStatus {
        CREATED,
        UPDATED,
        DELETED
    }

    @Override
    public String getType() {
        return type;
    }
}
