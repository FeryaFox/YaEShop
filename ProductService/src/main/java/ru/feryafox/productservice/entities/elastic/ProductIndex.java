package ru.feryafox.productservice.entities.elastic;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import ru.feryafox.productservice.entities.mongo.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "products")
public class ProductIndex {
    @Id
    private String id;

    private String name;
    private String description;
    private BigDecimal price;

    private String shopId;

    private Map<String, String> attributes;

    public static ProductIndex of(Product product) {
        ProductIndex.ProductIndexBuilder builder = ProductIndex.builder();

        builder.id(product.getId());
        builder.name(product.getName());
        builder.description(product.getDescription());
        builder.price(product.getPrice());
        builder.attributes(product.getAttributes());
        builder.shopId(product.getShop().getId());

        return builder.build();
    }
}
