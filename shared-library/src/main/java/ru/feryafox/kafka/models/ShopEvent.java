package ru.feryafox.kafka.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopEvent implements BaseKafkaModel {
    private final String type = "ShopEvent";
    private String shopId;
    private String shopName;
    private String ownerId;
    private ShopStatus shopStatus;

    public enum ShopStatus {
        CREATED,
        UPDATED,
        CLOSED
    }

    @Override
    public String getType() {
        return type;
    }
}
