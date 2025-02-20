package ru.feryafox.authservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.feryafox.authservice.entities.Buyer;
import ru.feryafox.authservice.entities.User;
import ru.feryafox.authservice.exceptions.user.UserIsNotExistException;
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
}
