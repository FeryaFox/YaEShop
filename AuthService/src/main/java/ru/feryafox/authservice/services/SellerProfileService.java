package ru.feryafox.authservice.services;

import lombok.RequiredArgsConstructor;
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
public class SellerProfileService {

    private final BaseService baseService;
    private final SellerRepository sellerRepository;

    public SellerProfileResponse getSellerProfile(String phone) {
        User user = baseService.getUser(phone);
        Optional<Seller> sellerOptional = sellerRepository.findById(user.getId());

        SellerProfileResponse sellerProfileResponse;
        if (sellerOptional.isPresent()) {
            sellerProfileResponse = SellerProfileResponse.from(user, sellerOptional.get());
        }
        else {
            sellerProfileResponse = SellerProfileResponse.from(user);
        }

        return sellerProfileResponse;
    }

    @Transactional
    public void updateSellerProfile(String phone, UpdateSellerProfile updateSellerProfile) {
        Seller seller = baseService.getSellerOrNull(phone);

        if (seller == null) {
            User user = baseService.getUser(phone);
            seller = new Seller();
            seller.setUser(user);
        }

        seller.setFullName(seller.getUser().getMiddleName() + " " + seller.getUser().getFirstName() +  seller.getUser().getSurname());
        seller.setBusinessLicense(updateSellerProfile.getBusinessLicense());
        seller.setShopName(updateSellerProfile.getShopName());

        sellerRepository.save(seller);
    }
}
