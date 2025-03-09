package ru.feryafox.shopservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.feryafox.shopservice.entitis.Shop;
import ru.feryafox.shopservice.exceptions.NoAccessToShop;
import ru.feryafox.shopservice.exceptions.ShopNotFound;
import ru.feryafox.shopservice.repositories.ShopRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BaseService {
    private final ShopRepository shopRepository;

    public Shop getShop(UUID shopId) {
        log.info("Поиск магазина по ID: {}", shopId);
        return shopRepository.findById(shopId).orElseThrow(() -> {
            log.warn("Магазин с ID {} не найден", shopId);
            return new ShopNotFound(shopId);
        });
    }

    public void isUserHasAccessToShop(UUID shopId, UUID userId) {
        log.info("Проверка доступа пользователя {} к магазину {}", userId, shopId);
        Shop shop = getShop(shopId);

        if (!shop.getUserOwner().equals(userId)) {
            log.warn("Пользователь {} не имеет доступа к магазину {}", userId, shopId);
            throw new NoAccessToShop(userId.toString(), shopId.toString());
        }

        log.info("Доступ к магазину {} пользователем {} подтвержден", shopId, userId);
    }
}
