package ru.feryafox.kafka.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewEvent implements BaseKafkaModel{
    private final String type = "ReviewEvent";

    private String reviewId;
    private String shopId;
    private String productId;
    private String productName;
    private String author;
    private int rating;
    private double avgProductRating;
    private long countProductReviews;
    private ReviewStatus status;

    public enum ReviewStatus {
       CREATED,
       UPDATED,
       DELETED
    }

    @Override
    public String getType() {
        return type;
    }
}
