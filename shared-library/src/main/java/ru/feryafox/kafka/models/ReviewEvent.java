package ru.feryafox.kafka.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewEvent implements BaseKafkaModel{
    private final String type = "ReviewEvent";

    private String reviewId;
    private String shopId;
    private String productId;
    private String productName;
    private String author;
    private int rating;
    private double avgProductRating;
    private ReviewStatus status;

    public enum ReviewStatus {
       CREATED,
       UPDATED,
       DELETED
    }
}
