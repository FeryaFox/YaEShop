package ru.feryafox.shopservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feryafox.shopservice.entitis.Shop;
import ru.feryafox.shopservice.exceptions.NoAccessToShop;
import ru.feryafox.shopservice.exceptions.ShopNotFound;
import ru.feryafox.shopservice.repositories.ShopRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BaseService {
    private final ShopRepository shopRepository;

    public Shop getShop(UUID shopId) {
        return shopRepository.findById(shopId).orElseThrow(() -> new ShopNotFound(shopId));
    }

    public void isUserHasAccessToShop(UUID shopId, UUID userId) {
        Shop shop = getShop(shopId);
        if (!shop.getUserOwner().equals(userId)) throw new NoAccessToShop(userId.toString(), shopId.toString());
    }
}
