package ru.feryafox.authservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.feryafox.authservice.entities.Buyer;
import ru.feryafox.authservice.entities.User;
import ru.feryafox.authservice.exceptions.user.UserIsNotExistException;
import ru.feryafox.authservice.models.requests.UpdateBuyerProfile;
import ru.feryafox.authservice.models.responses.BuyerProfileResponse;
import ru.feryafox.authservice.repositories.BuyerRepository;
import ru.feryafox.authservice.repositories.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BuyerProfileService {
    private final UserRepository userRepository;
    private final BuyerRepository buyerRepository;
    private final BaseService baseService;

    public BuyerProfileResponse getBuyerProfileService(String userPhone) {
        User user = baseService.getUser(userPhone);
        Optional<Buyer> buyerOptional = buyerRepository.findById(user.getId());

        BuyerProfileResponse buyerProfileResponse;
        if (buyerOptional.isPresent()) {
            buyerProfileResponse = BuyerProfileResponse.from(user, buyerOptional.get());
        } else {
            buyerProfileResponse = BuyerProfileResponse.from(user);
        }

        return buyerProfileResponse;
    }

    @Transactional
    public void updateBuyerProfile(String userPhone, UpdateBuyerProfile updateBuyerProfile) {
        Buyer buyer = baseService.getBuyerOrNull(userPhone);

        if (buyer == null) {
            User user = baseService.getUser(userPhone);
            buyer = new Buyer();
            buyer.setUser(user);
        }
        System.out.println(updateBuyerProfile);
        buyer.setFullName(buyer.getUser().getMiddleName() + " " + buyer.getUser().getFirstName() +  buyer.getUser().getSurname());
        buyer.setAddress(updateBuyerProfile.getAddress());
        buyer.setDateOfBirth(updateBuyerProfile.getDateOfBirth());

        buyerRepository.save(buyer);
    }
}
