package ru.feryafox.productservice.services.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.ProductEvent;
import ru.feryafox.kafka.models.ShopRatingEvent;
import ru.feryafox.productservice.entities.mongo.Product;
import ru.feryafox.productservice.entities.mongo.Shop;
import ru.feryafox.productservice.services.BaseService;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaService {
    private final BaseService baseService;
    private final KafkaProducerService kafkaProducerService;

    public ProductEvent convertProductToEvent(Product product, ProductEvent.ProductStatus status) {
        log.info("Конвертация продукта {} в событие ProductEvent со статусом {}", product.getId(), status);

        ProductEvent productEvent = new ProductEvent();
        productEvent.setProductId(product.getId());
        productEvent.setShopId(product.getShop().getId());
        productEvent.setOwnerId(product.getUserCreate());
        productEvent.setName(product.getName());
        productEvent.setStatus(status);
        productEvent.setPrice(Optional.ofNullable(product.getPrice()).map(Number::doubleValue).orElse(0.0));

        log.info("Создано событие ProductEvent: {}", productEvent);
        return productEvent;
    }

    private ShopRatingEvent createShopRatingEvent(String shopId, double shopRating) {
        log.info("Создание события ShopRatingEvent для магазина {} с рейтингом {}", shopId, shopRating);

        ShopRatingEvent shopRatingEvent = new ShopRatingEvent();
        shopRatingEvent.setShopId(shopId);
        shopRatingEvent.setShopRating(shopRating);

        return shopRatingEvent;
    }

    public void sendShopRating(String shopId) {
        log.info("Подготовка отправки рейтинга магазина {}", shopId);

        Shop shop = baseService.getShop(shopId);

        double avgRating = shop.getProducts().stream()
                .map(Product::getProductRating)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        log.info("Рассчитанный средний рейтинг для магазина {}: {}", shopId, avgRating);

        ShopRatingEvent shopRatingEvent = createShopRatingEvent(shopId, avgRating);
        kafkaProducerService.sendShopRatingEvent(shopRatingEvent);
        log.info("Событие ShopRatingEvent успешно отправлено в Kafka для магазина {}", shopId);
    }
}
