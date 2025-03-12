package ru.feryafox.authservice.controllers.profile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.authservice.models.requests.UpdateSellerProfile;
import ru.feryafox.authservice.models.responses.SellerProfileResponse;
import ru.feryafox.authservice.services.SellerProfileService;

@RestController
@RequestMapping("/profile/seller")
@RequiredArgsConstructor
@Tag(name = "Seller Profile", description = "API для управления профилем продавца")
public class SellerProfileController {
    private final SellerProfileService sellerProfileService;

    @Operation(summary = "Получение профиля продавца", responses = {
            @ApiResponse(responseCode = "200", description = "Профиль продавца успешно получен",
                    content = @Content(schema = @Schema(implementation = SellerProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content)
    })
    @GetMapping("")
    public ResponseEntity<?> getSellerProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok().body(sellerProfileService.getSellerProfile(userDetails.getUsername()));
    }

    @Operation(summary = "Обновление профиля продавца", responses = {
            @ApiResponse(responseCode = "204", description = "Профиль продавца успешно обновлён"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content)
    })
    @PostMapping("")
    public ResponseEntity<?> createSellerProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody(description = "Обновленные данные профиля", required = true) UpdateSellerProfile updateSellerProfile
    ) {
        sellerProfileService.updateSellerProfile(userDetails.getUsername(), updateSellerProfile);
        return ResponseEntity.noContent().build();
    }
}
