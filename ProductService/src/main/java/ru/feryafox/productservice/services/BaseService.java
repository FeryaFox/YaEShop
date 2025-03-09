package ru.feryafox.productservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.feryafox.productservice.entities.mongo.Product;
import ru.feryafox.productservice.entities.mongo.Shop;
import ru.feryafox.productservice.exceptions.NoAccessToTheProductException;
import ru.feryafox.productservice.exceptions.NoAccessToTheShopException;
import ru.feryafox.productservice.exceptions.ProductIsNotExist;
import ru.feryafox.productservice.exceptions.ShopIsNotExist;
import ru.feryafox.productservice.repositories.mongo.ProductRepository;
import ru.feryafox.productservice.repositories.mongo.ShopRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BaseService {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;

    public Shop getShop(String shopId) {
        log.info("Поиск магазина с ID: {}", shopId);

        return shopRepository.findById(shopId).orElseThrow(() -> {
            log.warn("Магазин с ID {} не найден", shopId);
            return new ShopIsNotExist(shopId);
        });
    }

    public Product getProduct(String productId) {
        log.info("Поиск продукта с ID: {}", productId);

        return productRepository.findById(productId).orElseThrow(() -> {
            log.warn("Продукт с ID {} не найден", productId);
            return new ProductIsNotExist(productId);
        });
    }

    public void isUserHasAccessToProduct(String productId, String userId) {
        log.info("Проверка доступа пользователя {} к продукту {}", userId, productId);

        Product product = getProduct(productId);
        if (!product.getUserCreate().equals(userId)) {
            log.warn("Пользователь {} не имеет доступа к продукту {}", userId, productId);
            throw new NoAccessToTheProductException(productId, userId);
        }

        log.info("Доступ пользователя {} к продукту {} подтвержден", userId, productId);
    }

    public void isUserHasAccessToShop(Shop shop, String userId) {
        log.info("Проверка доступа пользователя {} к магазину {}", userId, shop.getId());

        if (!shop.getUserOwner().equals(userId)) {
            log.warn("Пользователь {} не имеет доступа к магазину {}", userId, shop.getId());
            throw new NoAccessToTheShopException(shop.getId(), userId);
        }

        log.info("Доступ пользователя {} к магазину {} подтвержден", userId, shop.getId());
    }
}
