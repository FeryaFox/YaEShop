package ru.feryafox.productservice.entities.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "shops")
public class Shop {
    @Id
    private String id;

    private String userOwner;
    private String shopName;

    @DBRef
    private Set<Product> products = new HashSet<>();
}
