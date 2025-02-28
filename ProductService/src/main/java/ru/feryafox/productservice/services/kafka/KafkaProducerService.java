package ru.feryafox.productservice.services.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.feryafox.kafka.models.ProductEvent;
import ru.feryafox.kafka.models.ShopRatingEvent;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, ProductEvent> productKafkaTemplate;
    private final KafkaTemplate<String, ShopRatingEvent> shopRatingKafkaTemplate;

    public void sendProductEvent(ProductEvent productEvent) {
        productKafkaTemplate.send("product-topic", productEvent.getProductId(), productEvent);
    }

    public void sendShopRatingEvent(ShopRatingEvent shopRatingEvent) {
        shopRatingKafkaTemplate.send("shop-rating-topic", shopRatingEvent.getShopId(), shopRatingEvent);
    }
}
