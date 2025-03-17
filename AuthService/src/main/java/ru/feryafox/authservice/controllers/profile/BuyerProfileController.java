package ru.feryafox.authservice.controllers.profile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.authservice.models.requests.UpdateBuyerProfile;
import ru.feryafox.authservice.models.responses.BuyerProfileResponse;
import ru.feryafox.authservice.services.BuyerProfileService;

@RestController
@RequestMapping("/profile/buyer")
@RequiredArgsConstructor
@Tag(name = "Buyer Profile", description = "API для управления профилем покупателя")
public class BuyerProfileController {
    private final BuyerProfileService buyerProfileService;

    @Operation(summary = "Получение профиля покупателя", responses = {
            @ApiResponse(responseCode = "200", description = "Профиль покупателя успешно получен",
                    content = @Content(schema = @Schema(implementation = BuyerProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content)
    })
    @GetMapping("/")
    public ResponseEntity<BuyerProfileResponse> getBuyerProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(buyerProfileService.getBuyerProfileService(userDetails.getUsername()));
    }

    @Operation(summary = "Обновление профиля покупателя", responses = {
            @ApiResponse(responseCode = "204", description = "Профиль покупателя успешно обновлён"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content)
    })
    @PutMapping("/")
    public ResponseEntity<Void> updateBuyerProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateBuyerProfile updateBuyerProfile
    ) {
        buyerProfileService.updateBuyerProfile(userDetails.getUsername(), updateBuyerProfile);
        return ResponseEntity.noContent().build();
    }
}
