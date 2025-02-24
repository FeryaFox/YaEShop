package ru.feryafox.productservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.feryafox.productservice.entities.mongo.Shop;
import ru.feryafox.productservice.exceptions.ShopIsNotExist;
import ru.feryafox.productservice.repositories.mongo.ShopRepository;

@Service
@RequiredArgsConstructor
public class BaseService {

    private final ShopRepository shopRepository;

    public Shop getShop(String shopId) {
        return shopRepository.findById(shopId).orElseThrow(() -> new ShopIsNotExist(shopId));
    }
}
