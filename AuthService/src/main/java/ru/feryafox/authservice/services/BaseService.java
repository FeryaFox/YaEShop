package ru.feryafox.authservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BaseService {
    private final UserRepository userRepository;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;

    public User getUser(String phone) {
        log.info("Поиск пользователя по номеру телефона: {}", phone);
        return userRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> {
                    log.warn("Пользователь с номером {} не найден", phone);
                    return new UserIsNotExistException(phone);
                });
    }

    public Buyer getBuyer(String phone) {
        log.info("Поиск покупателя по номеру телефона: {}", phone);
        return buyerRepository.findByUser_PhoneNumber(phone)
                .orElseThrow(() -> {
                    log.warn("Покупатель с номером {} не найден", phone);
                    return new UserIsNotExistException(phone);
                });
    }

    public Buyer getBuyerOrNull(String phone) {
        log.info("Поиск покупателя по номеру телефона (может отсутствовать): {}", phone);
        Buyer buyer = buyerRepository.findByUser_PhoneNumber(phone).orElse(null);
        if (buyer == null) {
            log.warn("Покупатель с номером {} не найден", phone);
        } else {
            log.info("Найден покупатель: {}", phone);
        }
        return buyer;
    }

    public Seller getSellerOrNull(String phone) {
        log.info("Поиск продавца по номеру телефона (может отсутствовать): {}", phone);
        Seller seller = sellerRepository.findByUser_PhoneNumber(phone).orElse(null);
        if (seller == null) {
            log.warn("Продавец с номером {} не найден", phone);
        } else {
            log.info("Найден продавец: {}", phone);
        }
        return seller;
    }
}
