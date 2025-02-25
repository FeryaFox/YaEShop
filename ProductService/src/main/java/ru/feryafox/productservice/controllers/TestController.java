package ru.feryafox.productservice.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.feryafox.productservice.entities.mongo.Product;
import ru.feryafox.productservice.services.BaseService;

@RestController
@RequestMapping("/product/")
public class TestController {
    private final BaseService baseService;

    public TestController(BaseService baseService) {
        this.baseService = baseService;
    }

    @GetMapping("ping")
    public ResponseEntity<?> ping(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println(userDetails.getUsername());
        Product product = baseService.getProduct("67bdfe52bff7801b5e258ed0");
        System.out.println(product);
        return new ResponseEntity<>("pong", HttpStatus.OK);
    }
}
