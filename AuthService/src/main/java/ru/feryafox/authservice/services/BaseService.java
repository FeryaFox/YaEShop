package ru.feryafox.authservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feryafox.authservice.entities.Buyer;
import ru.feryafox.authservice.entities.Seller;
import ru.feryafox.authservice.entities.User;
import ru.feryafox.authservice.exceptions.user.UserIsNotExistException;
import ru.feryafox.authservice.repositories.BuyerRepository;
import ru.feryafox.authservice.repositories.SellerRepository;
import ru.feryafox.authservice.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class BaseService {
    private final UserRepository userRepository;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;

    public User getUser(String phone) {
       return userRepository.findByPhoneNumber(phone).orElseThrow(() -> new UserIsNotExistException(phone));
    }

    public Buyer getBuyer(String phone) {
        return buyerRepository.findByUser_PhoneNumber(phone).orElseThrow(() -> new UserIsNotExistException(phone));
    }

    public Buyer getBuyerOrNull(String phone) {
        return buyerRepository.findByUser_PhoneNumber(phone).orElse(null);
    }

    public Seller getSellerOrNull(String phone) {
        return sellerRepository.findByUser_PhoneNumber(phone).orElse(null);
    }
}
