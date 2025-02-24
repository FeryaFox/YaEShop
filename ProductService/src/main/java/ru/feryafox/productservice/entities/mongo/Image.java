package ru.feryafox.productservice.entities.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "images")
public class Image {
    @Id
    private String id;

    private String imageUrl;
    private int position;
    private String uploadedUser;

    @DBRef
    private Product product;
}
