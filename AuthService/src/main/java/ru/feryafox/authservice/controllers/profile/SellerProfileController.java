package ru.feryafox.authservice.controllers.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.authservice.models.requests.UpdateSellerProfile;
import ru.feryafox.authservice.services.SellerProfileService;

@RestController
@RequestMapping("/profile/seller")
@RequiredArgsConstructor
public class SellerProfileController {
    private final SellerProfileService sellerProfileService;

    @GetMapping("")
    public ResponseEntity<?> getSellerProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok().body(sellerProfileService.getSellerProfile(userDetails.getUsername()));
    }

    @PostMapping("")
    public ResponseEntity<?> createSellerProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateSellerProfile updateSellerProfile
    ) {
        sellerProfileService.updateSellerProfile(userDetails.getUsername(), updateSellerProfile);
        return ResponseEntity.noContent().build();
    }
}
