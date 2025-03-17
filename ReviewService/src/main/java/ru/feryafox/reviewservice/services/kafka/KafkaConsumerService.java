package ru.feryafox.reviewservice.services.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.ProductEvent;
import ru.feryafox.reviewservice.entities.Product;
import ru.feryafox.reviewservice.repositories.ProductRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final ProductRepository productRepository;

    @KafkaListener(topics = "product-topic", groupId = "review-service-group")
    public void listen(ConsumerRecord<String, Object> record) {
        log.info("Получено сообщение из Kafka (топик product-topic): {}", record.value());

        Object event = record.value();
        if (event instanceof ProductEvent productEvent) {
            process(productEvent);
        } else {
            log.warn("Получено неизвестное сообщение в product-topic: {}", event);
        }
    }

    private void process(ProductEvent productEvent) {
        log.info("Обработка ProductEvent: ID={}, Статус={}", productEvent.getProductId(), productEvent.getStatus());

        if (productEvent.getStatus() == ProductEvent.ProductStatus.CREATED) {
            log.info("Создание нового продукта с ID {}", productEvent.getProductId());
            var product = Product.from(productEvent);
            productRepository.save(product);
            log.info("Продукт {} успешно создан", product.getId());

        } else if (productEvent.getStatus() == ProductEvent.ProductStatus.UPDATED) {
            log.info("Обновление продукта с ID {}", productEvent.getProductId());

            Optional<Product> optionalProduct = productRepository.findById(productEvent.getProductId());
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                product.setName(productEvent.getName());
                product.setShop(productEvent.getShopId());
                product.setUserCreate(productEvent.getOwnerId());
                productRepository.save(product);
                log.info("Продукт {} успешно обновлен", product.getId());
            } else {
                log.warn("Продукт {} не найден в базе, обновление невозможно", productEvent.getProductId());
            }
        }
    }
}
