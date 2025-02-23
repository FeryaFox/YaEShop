package ru.feryafox.shopservice.beans;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.feryafox.kafka.models.ShopEvent;
import ru.feryafox.shopservice.services.kafka.KafkaProducerService;


@Configuration
public class KafkaTest {
    @Bean
    public CommandLineRunner createInitialAdmin(KafkaProducerService kafkaProducerService) {
        return args -> {
            System.out.println("Отправка.");
            ShopEvent shopEvent = new ShopEvent("id", "name", "status");
            kafkaProducerService.sendShopUpdate(shopEvent);
        };
    }
}
