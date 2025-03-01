package ru.feryafox.productservice.services.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.ReviewEvent;
import ru.feryafox.kafka.models.ShopEvent;
import ru.feryafox.productservice.entities.mongo.Shop;
import ru.feryafox.productservice.exceptions.ShopAndProductDontLinkedException;
import ru.feryafox.productservice.repositories.mongo.ProductRepository;
import ru.feryafox.productservice.repositories.mongo.ShopRepository;
import ru.feryafox.productservice.services.BaseService;
import ru.feryafox.productservice.services.ProductService;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final BaseService baseService;
    private final KafkaProducerService kafkaProducerService;
    private final KafkaService kafkaService;

    @KafkaListener(topics = "shop-topic", groupId = "product-service-group")
    public void listen(ConsumerRecord<String, Object> record) {

        Object event = record.value();

        if (event instanceof ShopEvent shopEvent) {

            if (shopEvent.getShopStatus().equals(ShopEvent.ShopStatus.CREATED)) {
               var shopOptional = shopRepository.findById(shopEvent.getShopId());
               if (shopOptional.isEmpty()) {
                   Shop shop = new Shop();
                   shop.setId(shopEvent.getShopId());
                   shop.setShopName(shopEvent.getShopName());
                   shop.setUserOwner(shopEvent.getOwnerId());

                   shopRepository.save(shop);

                   System.out.println("✅ Получено ShopEvent: " + shopEvent);
               }
               else {
                   System.out.println("Так получилось, что shop " + shopEvent.getShopId() +" уже в базе");
               }
            }
            else if (shopEvent.getShopStatus().equals(ShopEvent.ShopStatus.UPDATED)){
                var shopOptional = shopRepository.findById(shopEvent.getShopId());
                if (shopOptional.isPresent()) {
                    Shop shop = shopOptional.get();
                    shop.setShopName(shopEvent.getShopName());
                    shop.setUserOwner(shopEvent.getOwnerId());

                    shopRepository.save(shop);
                }
                else {
                    System.out.println("Shop" + shopEvent.getShopId() + " нет в базе, но мы пытаемся его обновить");
                    productService.getAndSaveShopFromShopService(shopEvent.getShopId());
                    System.out.println("Мы получили " + shopEvent.getShopId() + " и сохранили у себя в базе");
                }
            }

        } else {
            System.out.println("⚠️ Получен неизвестный тип события: " + event);
        }
    }


    @KafkaListener(topics = "review-topic", groupId = "pruduct-servuce-group")
    public void listenReview(ConsumerRecord<String, Object> record) {

        Object event = record.value();

        if (event instanceof ReviewEvent reviewEvent) {

            productService.updateProductRating(reviewEvent);


            // FIXME почему-то оправляется оценка магазина 0
            kafkaService.sendShopRating(reviewEvent.getShopId());
        } else {
            System.out.println("⚠️ Получен неизвестный тип события: " + event);
        }
    }
}
