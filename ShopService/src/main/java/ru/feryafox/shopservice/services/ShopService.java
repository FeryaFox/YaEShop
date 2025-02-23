package ru.feryafox.shopservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.feryafox.kafka.models.ShopEvent;
import ru.feryafox.shopservice.entitis.Shop;
import ru.feryafox.shopservice.exceptions.NoAccessToShop;
import ru.feryafox.shopservice.models.requests.CreateShopRequest;
import ru.feryafox.shopservice.models.requests.UpdateShopRequest;
import ru.feryafox.shopservice.models.responses.CreateShopResponse;
import ru.feryafox.shopservice.models.responses.ShopInfoResponse;
import ru.feryafox.shopservice.models.responses.UploadImageResponse;
import ru.feryafox.shopservice.repositories.ShopRepository;
import ru.feryafox.shopservice.services.kafka.KafkaProducerService;
import ru.feryafox.shopservice.services.minio.MinioService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final KafkaProducerService kafkaProducerService;
    private final BaseService baseService;
    private final MinioService minioService;

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

    @Transactional
    public UploadImageResponse uploadImage(MultipartFile file, UUID shopId, String userId) throws Exception {
        Shop shop = baseService.getShop(shopId);

        baseService.isUserHasAccessToShop(shopId, UUID.fromString(userId));

        String uploadedFilePath = minioService.uploadFile(file);

        shop.setShopImage(uploadedFilePath);
        shopRepository.save(shop);

        return new UploadImageResponse(uploadedFilePath);
    }

    @Transactional
    public void updateShop(UpdateShopRequest updateShopRequest, UUID shopId, String userId) {
        baseService.isUserHasAccessToShop(shopId, UUID.fromString(userId));

        Shop shop = baseService.getShop(shopId);
        shop.setShopName(updateShopRequest.getName());
        shop.setShopDescription(updateShopRequest.getDescription());

        ShopEvent shopEvent = new ShopEvent();
        shopEvent.setShopId(shopId.toString());
        shopEvent.setShopName(shop.getShopName());
        shopEvent.setOwnerId(userId);
        shopEvent.setShopStatus(ShopEvent.ShopStatus.UPDATED);
        kafkaProducerService.sendShopUpdate(shopEvent);

        shopRepository.save(shop);
    }
}
