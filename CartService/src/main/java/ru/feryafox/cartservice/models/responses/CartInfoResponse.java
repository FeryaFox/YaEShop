package ru.feryafox.cartservice.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.feryafox.cartservice.entities.CartItem;
import ru.feryafox.cartservice.entities.Product;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartInfoResponse {
    private double cartPrice;
    private Set<CartItemResponse> cartItems = new HashSet<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CartItemResponse {
        private String name;
        private String productId;
        private int quantity;
        private String shopId;
        private double price;

        public static CartItemResponse from(Product product, int quantity) {
            return CartItemResponse.builder()
                    .productId(product.getId())
                    .name(product.getName())
                    .quantity(quantity)
                    .shopId(product.getShopId())
                    .price(product.getPrice().doubleValue())
                    .build();
        }
    }
}
