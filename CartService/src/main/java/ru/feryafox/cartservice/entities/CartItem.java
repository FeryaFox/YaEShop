package ru.feryafox.cartservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.feryafox.kafka.models.OrderEvent;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem {
    private String productId;
    private int quantity;
    private BigDecimal price;
    private String shopId;

    public static OrderEvent.ProductItem toProductItem(CartItem cartItem) {
        return OrderEvent.ProductItem.builder()
                .productId(cartItem.productId)
                .price(cartItem.getPrice().doubleValue())
                .shopId(cartItem.shopId)
                .quantity(cartItem.quantity).build();
    }
}
