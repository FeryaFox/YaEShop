package ru.feryafox.productservice.entities.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import ru.feryafox.models.internal.responses.ShopInfoInternalResponse;

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
    @EqualsAndHashCode.Exclude
    private Set<Product> products = new HashSet<>();

    public static Shop from(ShopInfoInternalResponse shopInfoResponse) {
        Shop shop = new Shop();
        shop.setShopName(shopInfoResponse.getName());
        shop.setId(shop.getId());
        shop.setUserOwner(shopInfoResponse.getUserId());

        return shop;
    }
}
