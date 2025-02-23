package ru.feryafox.shopservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.feryafox.kafka.models.ShopEvent;
import ru.feryafox.shopservice.entitis.Shop;
import ru.feryafox.shopservice.models.requests.CreateShopRequest;
import ru.feryafox.shopservice.models.responses.CreateShopResponse;
import ru.feryafox.shopservice.models.responses.ShopInfoResponse;
import ru.feryafox.shopservice.repositories.ShopRepository;
import ru.feryafox.shopservice.services.kafka.KafkaProducerService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final KafkaProducerService kafkaProducerService;
    private final BaseService baseService;

    @Transactional
    public CreateShopResponse createShop(CreateShopRequest createShopRequest, String userId) {
        Shop shop = new Shop();
        shop.setShopName(createShopRequest.getName());
        if (createShopRequest.getDescription() != null) shop.setShopDescription(createShopRequest.getDescription());
        shop.setUserOwner(UUID.fromString(userId));
        shop = shopRepository.save(shop);

        ShopEvent shopEvent = new ShopEvent();
        shopEvent.setShopId(shop.getId().toString());
        shopEvent.setShopName(shop.getShopName());
        shopEvent.setOwnerId(userId);
        shopEvent.setShopStatus(ShopEvent.ShopStatus.CREATED);
        kafkaProducerService.sendShopUpdate(shopEvent);

        return new CreateShopResponse(shop.getId().toString());
    }

    public ShopInfoResponse getShopInfo(UUID shopId) {
        Shop shop = baseService.getShop(shopId);
        return ShopInfoResponse.from(shop);
    }
}
