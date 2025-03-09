package ru.feryafox.productservice.services.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.ReviewEvent;
import ru.feryafox.kafka.models.ShopEvent;
import ru.feryafox.productservice.entities.mongo.Shop;
import ru.feryafox.productservice.repositories.mongo.ShopRepository;
import ru.feryafox.productservice.services.ProductService;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final ShopRepository shopRepository;
    private final ProductService productService;
    private final KafkaService kafkaService;

    @KafkaListener(topics = "shop-topic", groupId = "product-service-group")
    public void listenShopEvent(ConsumerRecord<String, Object> record) {
        log.info("Получено сообщение из Kafka (топик shop-topic): {}", record.value());

        Object event = record.value();
        if (event instanceof ShopEvent shopEvent) {
            processShopEvent(shopEvent);
        } else {
            log.warn("Получено неизвестное событие в shop-topic: {}", event);
        }
    }

    private void processShopEvent(ShopEvent shopEvent) {
        log.info("Обработка ShopEvent: ID={}, Status={}", shopEvent.getShopId(), shopEvent.getShopStatus());

        if (shopEvent.getShopStatus() == ShopEvent.ShopStatus.CREATED) {
            shopRepository.findById(shopEvent.getShopId()).ifPresentOrElse(
                    shop -> log.info("Магазин {} уже существует в базе, пропускаем создание.", shopEvent.getShopId()),
                    () -> {
                        Shop shop = new Shop();
                        shop.setId(shopEvent.getShopId());
                        shop.setShopName(shopEvent.getShopName());
                        shop.setUserOwner(shopEvent.getOwnerId());
                        shopRepository.save(shop);
                        log.info("Магазин {} успешно сохранен в базе", shopEvent.getShopId());
                    }
            );
        } else if (shopEvent.getShopStatus() == ShopEvent.ShopStatus.UPDATED) {
            shopRepository.findById(shopEvent.getShopId()).ifPresentOrElse(
                    shop -> {
                        shop.setShopName(shopEvent.getShopName());
                        shop.setUserOwner(shopEvent.getOwnerId());
                        shopRepository.save(shop);
                        log.info("Магазин {} успешно обновлен", shopEvent.getShopId());
                    },
                    () -> {
                        log.warn("Магазин {} не найден в базе, загружаем из ShopService...", shopEvent.getShopId());
                        productService.getAndSaveShopFromShopService(shopEvent.getShopId());
                        log.info("Магазин {} успешно загружен и сохранен", shopEvent.getShopId());
                    }
            );
        }
    }

    @KafkaListener(topics = "review-topic", groupId = "product-service-group")
    public void listenReviewEvent(ConsumerRecord<String, Object> record) {
        log.info("Получено сообщение из Kafka (топик review-topic): {}", record.value());

        Object event = record.value();
        if (event instanceof ReviewEvent reviewEvent) {
            log.info("Обновление рейтинга продукта {} и магазина {}", reviewEvent.getProductId(), reviewEvent.getShopId());
            productService.updateProductRating(reviewEvent);
            kafkaService.sendShopRating(reviewEvent.getShopId());
        } else {
            log.warn("Получено неизвестное событие в review-topic: {}", event);
        }
    }
}
