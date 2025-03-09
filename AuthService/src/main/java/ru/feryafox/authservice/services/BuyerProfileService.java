package ru.feryafox.authservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.feryafox.authservice.entities.Buyer;
import ru.feryafox.authservice.entities.User;
import ru.feryafox.authservice.models.requests.UpdateBuyerProfile;
import ru.feryafox.authservice.models.responses.BuyerProfileResponse;
import ru.feryafox.authservice.repositories.BuyerRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuyerProfileService {
    private final BuyerRepository buyerRepository;
    private final BaseService baseService;

    public BuyerProfileResponse getBuyerProfileService(String userPhone) {
        log.info("Запрос профиля покупателя для номера: {}", userPhone);

        User user = baseService.getUser(userPhone);
        Optional<Buyer> buyerOptional = buyerRepository.findById(user.getId());

        BuyerProfileResponse buyerProfileResponse = buyerOptional
                .map(buyer -> {
                    log.info("Покупатель найден в базе данных: {}", userPhone);
                    return BuyerProfileResponse.from(user, buyer);
                })
                .orElseGet(() -> {
                    log.warn("Покупатель не найден в базе данных, но пользователь {} существует", userPhone);
                    return BuyerProfileResponse.from(user);
                });

        log.info("Отправка профиля покупателя для номера: {}", userPhone);
        return buyerProfileResponse;
    }

    @Transactional
    public void updateBuyerProfile(String userPhone, UpdateBuyerProfile updateBuyerProfile) {
        log.info("Обновление профиля покупателя для номера: {}", userPhone);

        Buyer buyer = baseService.getBuyerOrNull(userPhone);
        if (buyer == null) {
            log.warn("Покупатель не найден, создаем нового покупателя для {}", userPhone);
            User user = baseService.getUser(userPhone);
            buyer = new Buyer();
            buyer.setUser(user);
        }

        buyer.setFullName(buyer.getUser().getMiddleName() + " " + buyer.getUser().getFirstName() + " " + buyer.getUser().getSurname());
        buyer.setAddress(updateBuyerProfile.getAddress());
        buyer.setDateOfBirth(updateBuyerProfile.getDateOfBirth());

        buyerRepository.save(buyer);
        log.info("Профиль покупателя {} успешно обновлен", userPhone);
    }
}
