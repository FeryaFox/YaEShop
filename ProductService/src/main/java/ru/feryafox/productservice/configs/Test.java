package ru.feryafox.productservice.configs;

import jakarta.persistence.Column;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.feryafox.productservice.entities.Product;
import ru.feryafox.productservice.entities.Shop;
import ru.feryafox.productservice.repositories.ProductRepository;
import ru.feryafox.productservice.repositories.ShopRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.UUID;

@Configuration
public class Test {
    @Bean
    public CommandLineRunner createInitialAdmin(ShopRepository shopRepository, ProductRepository productRepository) {
        return args -> {
            Shop shop = shopRepository.findById("4625bc90-222e-4164-b5de-ed883dd49554").get();

            Product product = new Product();
            product.setDescription("Product Description");
            product.setName("Product Name");

            product.setPrice(BigDecimal.TEN);

            var foo = new HashMap<String, String>();
            foo.put("foo", "foo");
            product.setAttributes(foo);


            shop.getProducts().add(product);

            productRepository.save(product);
            shopRepository.save(shop);
        };
    };
}
