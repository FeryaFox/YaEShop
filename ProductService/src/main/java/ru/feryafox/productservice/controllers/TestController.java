package ru.feryafox.productservice.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product/")
public class TestController {
    @GetMapping("ping")
    public ResponseEntity<?> ping(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println(userDetails.getUsername());
        return new ResponseEntity<>("pong", HttpStatus.OK);
    }
}
