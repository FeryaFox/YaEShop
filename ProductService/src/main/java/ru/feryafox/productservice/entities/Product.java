package ru.feryafox.productservice.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "products")
public class Product {
    @Id
    private String id;

    private String name;
    private String description;
    private BigDecimal price;

    @DBRef
    private Shop shop;

    @DBRef
    private Set<Image> images;

    private Map<String, String> attributes;
    private LocalDateTime createdAt = LocalDateTime.now();
}
