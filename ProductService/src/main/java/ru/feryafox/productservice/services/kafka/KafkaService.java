package ru.feryafox.productservice.services.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.ProductEvent;
import ru.feryafox.kafka.models.ShopRatingEvent;
import ru.feryafox.productservice.entities.mongo.Product;
import ru.feryafox.productservice.entities.mongo.Shop;
import ru.feryafox.productservice.services.BaseService;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class KafkaService {
    private final BaseService baseService;
    private final KafkaProducerService kafkaProducerService;

    public ProductEvent convertProductToEvent(Product product, ProductEvent.ProductStatus status) {
        ProductEvent productEvent = new ProductEvent();
        productEvent.setProductId(product.getId());
        productEvent.setShopId(product.getShop().getId());
        productEvent.setOwnerId(product.getUserCreate());
        productEvent.setName(product.getName());
        productEvent.setStatus(status);

        return productEvent;
    }

    private ShopRatingEvent createShopRatingEvent(String shopId, double shopRating) {
        ShopRatingEvent shopRatingEvent = new ShopRatingEvent();
        shopRatingEvent.setShopId(shopId);
        shopRatingEvent.setShopRating(shopRating);

        return shopRatingEvent;
    }

    public void sendShopRating(String shopId) {
        Shop shop = baseService.getShop(shopId);

        double avgRating = shop.getProducts().stream()
                .map(Product::getProductRating)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        ShopRatingEvent shopRatingEvent = createShopRatingEvent(shopId, avgRating);
        kafkaProducerService.sendShopRatingEvent(shopRatingEvent);
    }
}
