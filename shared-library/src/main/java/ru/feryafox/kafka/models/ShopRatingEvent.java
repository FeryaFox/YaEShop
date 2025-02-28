package ru.feryafox.kafka.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopRatingEvent implements BaseKafkaModel {
    private final String type = "ShopRatingEvent";

    private String shopId;
    private double shopRating;
}
