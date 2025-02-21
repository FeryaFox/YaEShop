package ru.feryafox.authservice.controllers.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.authservice.models.requests.UpdateBuyerProfile;
import ru.feryafox.authservice.services.BuyerProfileService;

@RestController
@RequestMapping("/profile/buyer")
@RequiredArgsConstructor
public class BuyerProfileController {
    private final BuyerProfileService buyerProfileService;

    @GetMapping("")
    public ResponseEntity<?> getBuyerProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok().body(buyerProfileService.getBuyerProfileService(userDetails.getUsername()));
    }

    @PostMapping("")
    public ResponseEntity<?> createBuyerProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateBuyerProfile updateBuyerProfile
    ) {
        buyerProfileService.updateBuyerProfile(userDetails.getUsername(), updateBuyerProfile);
        return ResponseEntity.noContent().build();
    }
}
