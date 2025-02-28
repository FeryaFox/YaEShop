package ru.feryafox.productservice.entities.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import ru.feryafox.productservice.models.requests.CreateProductRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
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

    private String userCreate;

    private double productRating;
    private long countProductReviews;

    @DBRef
    @EqualsAndHashCode.Exclude
    private Shop shop;


    @DBRef
    @EqualsAndHashCode.Exclude
    private Set<Image> images = new HashSet<>();

    private Map<String, String> attributes;

    private LocalDateTime createdAt = LocalDateTime.now();

    public static Product from(CreateProductRequest createProductRequest) {
        Product product = new Product();
        product.setName(createProductRequest.getName());
        product.setDescription(createProductRequest.getDescription());
        product.setPrice(BigDecimal.valueOf(createProductRequest.getPrice()));
        product.setAttributes(createProductRequest.getAttributes());

        return product;
    }
}
