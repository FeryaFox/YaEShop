package ru.feryafox.cartservice.models.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.feryafox.cartservice.entities.Product;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Информация о корзине пользователя")
public class CartInfoResponse {

    @Schema(description = "Общая стоимость корзины", example = "1250.50")
    private double cartPrice;

    @Schema(description = "Список товаров в корзине")
    private Set<CartItemResponse> cartItems = new HashSet<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "Информация о товаре в корзине")
    public static class CartItemResponse {

        @Schema(description = "Название товара", example = "Смартфон Samsung Galaxy S21")
        private String name;

        @Schema(description = "Идентификатор товара", example = "12345abcde")
        private String productId;

        @Schema(description = "Количество товара в корзине", example = "2")
        private int quantity;

        @Schema(description = "Идентификатор магазина", example = "shop56789")
        private String shopId;

        @Schema(description = "Цена товара", example = "625.25")
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
