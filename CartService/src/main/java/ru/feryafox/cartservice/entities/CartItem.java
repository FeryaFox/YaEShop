package ru.feryafox.cartservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.feryafox.kafka.models.OrderEvent;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem {
    private String productId;
    private int quantity;

    public static OrderEvent.ProductItem toProductItem(CartItem cartItem) {
        return OrderEvent.ProductItem.builder()
                .productId(cartItem.productId)
                .quantity(cartItem.quantity).build();
    }
}
