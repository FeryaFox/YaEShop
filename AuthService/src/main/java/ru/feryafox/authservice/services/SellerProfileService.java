package ru.feryafox.authservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.feryafox.authservice.entities.Seller;
import ru.feryafox.authservice.entities.User;
import ru.feryafox.authservice.models.requests.UpdateSellerProfile;
import ru.feryafox.authservice.models.responses.SellerProfileResponse;
import ru.feryafox.authservice.repositories.SellerRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerProfileService {

    private final BaseService baseService;
    private final SellerRepository sellerRepository;

    public SellerProfileResponse getSellerProfile(String phone) {
        log.info("Запрос профиля продавца для номера: {}", phone);

        User user = baseService.getUser(phone);
        Optional<Seller> sellerOptional = sellerRepository.findById(user.getId());

        SellerProfileResponse sellerProfileResponse = sellerOptional
                .map(seller -> {
                    log.info("Продавец найден в базе данных: {}", phone);
                    return SellerProfileResponse.from(user, seller);
                })
                .orElseGet(() -> {
                    log.warn("Продавец не найден в базе данных, но пользователь {} существует", phone);
                    return SellerProfileResponse.from(user);
                });

        log.info("Отправка профиля продавца для номера: {}", phone);
        return sellerProfileResponse;
    }

    @Transactional
    public void updateSellerProfile(String phone, UpdateSellerProfile updateSellerProfile) {
        log.info("Обновление профиля продавца для номера: {}", phone);

        Seller seller = baseService.getSellerOrNull(phone);
        if (seller == null) {
            log.warn("Продавец не найден, создаем нового продавца для {}", phone);
            User user = baseService.getUser(phone);
            seller = new Seller();
            seller.setUser(user);
        }

        seller.setFullName(seller.getUser().getMiddleName() + " " + seller.getUser().getFirstName() + " " + seller.getUser().getSurname());
        seller.setBusinessLicense(updateSellerProfile.getBusinessLicense());
        seller.setShopName(updateSellerProfile.getShopName());

        sellerRepository.save(seller);
        log.info("Профиль продавца {} успешно обновлен", phone);
    }
}
