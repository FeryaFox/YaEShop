package ru.feryafox.reviewservice.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.feryafox.kafka.models.ProductEvent;

import java.math.BigDecimal;
import java.util.HashSet;
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

    private String userCreate;

    private String shop;

    @DBRef
    @EqualsAndHashCode.Exclude
    private Set<Review> reviews = new HashSet<>();

    public static Product from(ProductEvent event) {
        Product product = new Product();
        product.setId(event.getProductId());
        product.setName(event.getName());
        product.setUserCreate(event.getOwnerId());
        product.setShop(event.getShopId());
        return product;
    }
}
