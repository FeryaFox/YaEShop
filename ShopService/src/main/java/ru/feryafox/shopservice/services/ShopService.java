package ru.feryafox.shopservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.feryafox.kafka.models.ShopEvent;
import ru.feryafox.kafka.models.ShopRatingEvent;
import ru.feryafox.models.internal.responses.ShopInfoInternalResponse;
import ru.feryafox.shopservice.entitis.Shop;
import ru.feryafox.shopservice.models.requests.CreateShopRequest;
import ru.feryafox.shopservice.models.requests.UpdateShopRequest;
import ru.feryafox.shopservice.models.responses.CreateShopResponse;
import ru.feryafox.shopservice.models.responses.ShopInfoResponse;
import ru.feryafox.shopservice.models.responses.UploadImageResponse;
import ru.feryafox.shopservice.repositories.ShopRepository;
import ru.feryafox.shopservice.services.kafka.KafkaProducerService;
import ru.feryafox.shopservice.services.minio.MinioService;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopService {

    private final ShopRepository shopRepository;
    private final KafkaProducerService kafkaProducerService;
    private final BaseService baseService;
    private final MinioService minioService;

    @Transactional
    public CreateShopResponse createShop(CreateShopRequest createShopRequest, String userId) {
        log.info("Создание нового магазина пользователем {}", userId);

        Shop shop = new Shop();
        shop.setShopName(createShopRequest.getName());
        if (createShopRequest.getDescription() != null) shop.setShopDescription(createShopRequest.getDescription());
        shop.setUserOwner(UUID.fromString(userId));
        shop = shopRepository.save(shop);

        log.info("Магазин {} успешно создан пользователем {}", shop.getId(), userId);

        ShopEvent shopEvent = new ShopEvent();
        shopEvent.setShopId(shop.getId().toString());
        shopEvent.setShopName(shop.getShopName());
        shopEvent.setOwnerId(userId);
        shopEvent.setShopStatus(ShopEvent.ShopStatus.CREATED);

        kafkaProducerService.sendShopUpdate(shopEvent);
        log.info("Событие ShopEvent отправлено в Kafka для магазина {}", shop.getId());

        return new CreateShopResponse(shop.getId().toString());
    }

    public ShopInfoResponse getShopInfo(UUID shopId) {
        log.info("Запрос информации о магазине {}", shopId);
        Shop shop = baseService.getShop(shopId);
        return ShopInfoResponse.from(shop);
    }

    public ShopInfoInternalResponse getInternalShopInfo(UUID shopId) {
        log.info("Запрос внутренней информации о магазине {}", shopId);
        Shop shop = baseService.getShop(shopId);
        return Shop.toShopInfoInternalResponse(shop);
    }

    @Transactional
    public void updateShopRatingFromEvent(ShopRatingEvent ratingEvent) {
        log.info("Обновление рейтинга магазина {} (новый рейтинг: {})", ratingEvent.getShopId(), ratingEvent.getShopRating());

        Shop shop = baseService.getShop(UUID.fromString(ratingEvent.getShopId()));
        shop.setRating(BigDecimal.valueOf(ratingEvent.getShopRating()));
        shopRepository.save(shop);

        log.info("Рейтинг магазина {} успешно обновлен", shop.getId());
    }

    @Transactional
    public UploadImageResponse uploadImage(MultipartFile file, UUID shopId, String userId) {
        log.info("Загрузка изображения для магазина {} пользователем {}", shopId, userId);

        Shop shop = baseService.getShop(shopId);
        baseService.isUserHasAccessToShop(shopId, UUID.fromString(userId));

        try {
            String uploadedFilePath = minioService.uploadImage(file);
            shop.setShopImage(uploadedFilePath);
            shopRepository.save(shop);
            log.info("Изображение загружено для магазина {}: {}", shopId, uploadedFilePath);
            return new UploadImageResponse(uploadedFilePath);
        } catch (Exception e) {
            log.error("Ошибка загрузки изображения для магазина {}: {}", shopId, e.getMessage(), e);
            throw new RuntimeException("Ошибка загрузки изображения", e);
        }
    }

    @Transactional
    public void updateShop(UpdateShopRequest updateShopRequest, UUID shopId, String userId) {
        log.info("Обновление данных магазина {} пользователем {}", shopId, userId);

        baseService.isUserHasAccessToShop(shopId, UUID.fromString(userId));
        Shop shop = baseService.getShop(shopId);

        shop.setShopName(updateShopRequest.getName());
        shop.setShopDescription(updateShopRequest.getDescription());
        shopRepository.save(shop);

        log.info("Магазин {} успешно обновлен пользователем {}", shopId, userId);

        ShopEvent shopEvent = new ShopEvent();
        shopEvent.setShopId(shopId.toString());
        shopEvent.setShopName(shop.getShopName());
        shopEvent.setOwnerId(userId);
        shopEvent.setShopStatus(ShopEvent.ShopStatus.UPDATED);

        kafkaProducerService.sendShopUpdate(shopEvent);
        log.info("Событие ShopEvent отправлено в Kafka для обновленного магазина {}", shopId);
    }
}
