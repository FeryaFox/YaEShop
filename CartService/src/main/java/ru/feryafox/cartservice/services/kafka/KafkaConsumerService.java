package ru.feryafox.cartservice.services.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.feryafox.cartservice.entities.Product;
import ru.feryafox.cartservice.repositories.ProductRepository;
import ru.feryafox.kafka.models.ProductEvent;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final ProductRepository productRepository;

    @KafkaListener(topics = "product-topic")
    public void listen(ConsumerRecord<String, Object> record) {
        log.info("Получено сообщение из Kafka: {}", record.value());

        Object event = record.value();

        if (event instanceof ProductEvent productEvent) {
            process(productEvent);
        } else {
            log.warn("Получено неизвестное сообщение: {}", event);
        }
    }

    private void process(ProductEvent productEvent) {
        log.info("Обработка события: {}", productEvent);

        switch (productEvent.getStatus()) {
            case CREATED -> handleProductCreated(productEvent);
            case UPDATED -> handleProductUpdated(productEvent);
            default -> log.warn("Неизвестный статус продукта: {}", productEvent.getStatus());
        }
    }

    private void handleProductCreated(ProductEvent productEvent) {
        Product product = Product.from(productEvent);
        log.info("Создание нового продукта: {}", product.getId());
        productRepository.save(product);
    }

    private void handleProductUpdated(ProductEvent productEvent) {
        log.info("Обновление продукта: {}", productEvent.getProductId());

        Optional<Product> optionalProduct = productRepository.findById(productEvent.getProductId());
        if (optionalProduct.isEmpty()) {
            log.warn("Продукт {} не найден, невозможно обновить", productEvent.getProductId());
            return;
        }

        Product product = optionalProduct.get();
        product.setName(productEvent.getName());
        product.setShopId(productEvent.getShopId());
        product.setOwnerId(productEvent.getOwnerId());
        product.setPrice(BigDecimal.valueOf(productEvent.getPrice()));

        log.info("Продукт {} обновлен успешно", product.getId());
        productRepository.save(product);
    }
}
