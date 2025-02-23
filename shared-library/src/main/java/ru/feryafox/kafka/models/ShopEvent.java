package ru.feryafox.kafka.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopEvent {
    private String shopId;
    private String shopName;
    private String ownerId;
    private ShopStatus shopStatus;

    public enum ShopStatus {
        CREATED,
        UPDATED,
        CLOSED
    }
}
