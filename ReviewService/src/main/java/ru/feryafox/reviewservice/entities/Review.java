package ru.feryafox.reviewservice.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.feryafox.reviewservice.models.requests.CreateReviewRequest;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "reviews")
public class Review {
    @Id
    private String id;

    private String positive;
    private String negative;
    private String comment;

    private String author;

    private int rating;

    @DBRef
    @EqualsAndHashCode.Exclude
    private Product product;

    private LocalDateTime createdAt = LocalDateTime.now();
}