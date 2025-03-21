package ru.feryafox.orderservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import ru.feryafox.kafka.models.OrderEvent;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "product_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductItem {
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false, updatable = false)
    private String productId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "shop_id", nullable = false)
    private UUID shopId;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public static Set<ProductItem> createProductsFromEvent(Set<OrderEvent.ProductItem> productItemsEvent, Order order) {
        return productItemsEvent.stream().map(eventItem ->
                ProductItem.builder()
                        .productId(eventItem.getProductId())
                        .quantity(eventItem.getQuantity())
                        .shopId(UUID.fromString(eventItem.getShopId()))
                        .price(BigDecimal.valueOf(eventItem.getPrice()))
                        .order(order)
                        .build()
        ).collect(Collectors.toSet());
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ProductItem that = (ProductItem) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "quantity = " + quantity + ", " +
                "shopId = " + shopId + ", " +
                "price = " + price + ")";
    }
}
