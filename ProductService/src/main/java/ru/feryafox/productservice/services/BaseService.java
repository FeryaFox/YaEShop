package ru.feryafox.productservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.ProductEvent;
import ru.feryafox.kafka.models.ShopRatingEvent;
import ru.feryafox.productservice.entities.mongo.Product;
import ru.feryafox.productservice.entities.mongo.Shop;
import ru.feryafox.productservice.exceptions.NoAccessToTheProductException;
import ru.feryafox.productservice.exceptions.NoAccessToTheShopException;
import ru.feryafox.productservice.exceptions.ProductIsNotExist;
import ru.feryafox.productservice.exceptions.ShopIsNotExist;
import ru.feryafox.productservice.repositories.mongo.ProductRepository;
import ru.feryafox.productservice.repositories.mongo.ShopRepository;
import ru.feryafox.productservice.services.kafka.KafkaProducerService;

@Service
@RequiredArgsConstructor
public class BaseService {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final KafkaProducerService kafkaProducerService;

    public Shop getShop(String shopId) {
        return shopRepository.findById(shopId).orElseThrow(() -> new ShopIsNotExist(shopId));
    }

    public Product getProduct(String productId) {
        return productRepository.findById(productId).orElseThrow(() -> new ProductIsNotExist(productId));
    }

    public void isUserHasAccessToProduct(String productId, String userId) {
        Product product = getProduct(productId);
        if (!product.getUserCreate().equals(userId)) throw new NoAccessToTheProductException(productId, userId);
    }

    public void isUserHasAccessToShop(Shop shop, String userId) {
        if (!shop.getUserOwner().equals(userId)) throw new NoAccessToTheShopException(shop.getId(), userId);
    }

}
