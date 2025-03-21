package ru.feryafox.shopservice.entitis;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import ru.feryafox.models.internal.responses.ShopInfoInternalResponse;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "shops")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shop {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_owner", nullable = false)
    private UUID userOwner;

    @Column(name = "shop_name", nullable = false)
    private String shopName;

    @Column(name = "shop_description")
    private String shopDescription;

    @Column(name = "shop_image")
    private String shopImage;

    @Column(name = "rating")
    private BigDecimal rating;

    public static ShopInfoInternalResponse toShopInfoInternalResponse(Shop shop) {

        ShopInfoInternalResponse shopInfoInternalResponse = new ShopInfoInternalResponse();

        shopInfoInternalResponse.setShopId(String.valueOf(shop.getId()));
        shopInfoInternalResponse.setUserId(String.valueOf(shop.getUserOwner()));
        shopInfoInternalResponse.setName(shop.getShopName());
        shopInfoInternalResponse.setDescription(shop.getShopDescription());
        shopInfoInternalResponse.setImage(shop.getShopImage());

        return shopInfoInternalResponse;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "userOwner = " + userOwner + ", " +
                "shopName = " + shopName + ", " +
                "shopDescription = " + shopDescription + ", " +
                "shopImage = " + shopImage + ", " +
                "rating = " + rating + ")";
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Shop shop = (Shop) o;
        return getId() != null && Objects.equals(getId(), shop.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
