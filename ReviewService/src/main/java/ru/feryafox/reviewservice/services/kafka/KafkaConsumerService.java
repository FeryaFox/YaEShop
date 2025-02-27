package ru.feryafox.reviewservice.services.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.ProductEvent;
import ru.feryafox.reviewservice.entities.Product;
import ru.feryafox.reviewservice.repositories.ProductRepository;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final ProductRepository productRepository;

    @KafkaListener(topics = "product-topic", groupId = "review-service-group")
    public void listen(ConsumerRecord<String, Object> record) {
        Object event = record.value();

        if (event instanceof ProductEvent productEvent) {
            process(productEvent);
        } else {
            System.out.println("Получено неизвестное сообщение");
        }

    }

    private void process(ProductEvent productEvent) {
        if (productEvent.getStatus().equals(ProductEvent.ProductStatus.CREATED)) {
            var product = Product.from(productEvent);
            System.out.println("Создаем продукт");
            productRepository.save(product);
        } else if (productEvent.getStatus().equals(ProductEvent.ProductStatus.UPDATED)) {
            var product = productRepository.findById(productEvent.getProductId()).get();
            product.setName(productEvent.getName());
            product.setShop(productEvent.getShopId());
            product.setUserCreate(productEvent.getOwnerId());
            System.out.println("Обновляем продукт");
            productRepository.save(product);
        }
    }
}
