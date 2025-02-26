package ru.feryafox.productservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.feryafox.productservice.entities.mongo.Product;
import ru.feryafox.productservice.entities.mongo.Shop;
import ru.feryafox.productservice.exceptions.NoAccessToTheProductException;
import ru.feryafox.productservice.exceptions.NoAccessToTheShopException;
import ru.feryafox.productservice.exceptions.ProductIsNotExist;
import ru.feryafox.productservice.exceptions.ShopIsNotExist;
import ru.feryafox.productservice.repositories.mongo.ProductRepository;
import ru.feryafox.productservice.repositories.mongo.ShopRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BaseService {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;

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
//    public Shop getShopFromShopService(String shopId) {
//        try {
//
//        }
//    }
}
