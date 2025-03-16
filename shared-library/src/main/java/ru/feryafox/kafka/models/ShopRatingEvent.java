package ru.feryafox.kafka.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopRatingEvent implements BaseKafkaModel {
    private final String type = "ShopRatingEvent";

    private String shopId;
    private double shopRating;

    @Override
    public String getType() {
       return type;
    }
}
