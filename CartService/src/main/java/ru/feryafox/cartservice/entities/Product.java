package ru.feryafox.cartservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private String shopId;
    private String ownerId;
    private BigDecimal price;

    public static Product from(ProductEvent event) {
        var builder = Product.builder();

        builder.id(event.getProductId())
                .name(event.getName())
                .shopId(event.getShopId())
                .ownerId(event.getOwnerId())
                .price(BigDecimal.valueOf(event.getPrice()));

        return builder.build();
    }
}