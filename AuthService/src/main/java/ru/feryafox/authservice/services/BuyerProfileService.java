package ru.feryafox.authservice.services;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class BuyerProfileService {
    public BuyerProfileService getBuyerProfileService(String userId) {

        System.out.println(userId);
        return null;
    }
}
