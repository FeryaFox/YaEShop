package ru.feryafox.authservice.controllers.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.feryafox.authservice.services.BuyerProfileService;

@RestController
@RequestMapping("/profile/buyer")
@RequiredArgsConstructor
public class BuyerProfileController {
    private final BuyerProfileService buyerProfileService;

    @GetMapping("")
    public ResponseEntity<String> getBuyerProfile(@AuthenticationPrincipal UserDetails userDetails) {
        buyerProfileService.getBuyerProfileService(userDetails.getUsername());
        return null;
    }
}
