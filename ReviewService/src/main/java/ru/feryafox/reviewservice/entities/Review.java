package ru.feryafox.reviewservice.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "reviews")
public class Review {
    @Id
    private String id;

    private String title;
    private String content;
    private String author;

    private int rating;

    @DBRef
    @EqualsAndHashCode.Exclude
    private Product product;

    private LocalDateTime createdAt = LocalDateTime.now();
}
