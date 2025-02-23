package ru.feryafox.shopservice.configs;

import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaProducerConfig {

//    @Bean
//    public ProducerFactory<String, ShopEvent> producerFactory() {
//        Map<String, Object> config = new HashMap<>();
//        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
//        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//
//        return new DefaultKafkaProducerFactory<>(config);
//    }
//
//    @Bean
//    public KafkaTemplate<String, ShopEvent> kafkaTemplate() {
//        return new KafkaTemplate<>(producerFactory());
//    }
}
