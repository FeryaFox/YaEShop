package ru.feryafox.authservice.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.feryafox.authservice.entities.RefreshToken;
import ru.feryafox.authservice.entities.Role;
import ru.feryafox.authservice.entities.User;
import ru.feryafox.authservice.exceptions.token.RefreshTokenIsNotExistException;
import ru.feryafox.authservice.exceptions.user.UserIsExistException;
import ru.feryafox.authservice.models.requests.LoginRequest;
import ru.feryafox.authservice.models.requests.RegisterRequest;
import ru.feryafox.authservice.models.requests.RegisterRequestDelivery;
import ru.feryafox.authservice.models.responses.AuthResponse;
import ru.feryafox.authservice.repositories.RefreshTokenRepository;
import ru.feryafox.authservice.repositories.RoleRepository;
import ru.feryafox.authservice.repositories.UserRepository;
import ru.feryafox.authservice.services.kafka.KafkaService;
import ru.feryafox.authservice.utils.IpAddressUtils;
import ru.feryafox.jwt.JwtUtils;
import ru.feryafox.kafka.NotificationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final KafkaService kafkaService;
    private final NotificationService notificationService;

    @Transactional
    public void register(RegisterRequest registerRequest) {
        log.info("Начало регистрации пользователя с номером: {}", registerRequest.getPhoneNumber());

        if (userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())) {
            log.warn("Ошибка регистрации: пользователь с номером {} уже существует", registerRequest.getPhoneNumber());
            throw new UserIsExistException(registerRequest.getPhoneNumber());
        }

        User user = new User();
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setFirstName(registerRequest.getFirstName());
        user.setSurname(registerRequest.getSurname());
        user.setMiddleName(registerRequest.getMiddleName());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));

        Role role = roleRepository.findByName(registerRequest.getRole().equals("SELLER") ? Role.RoleName.ROLE_SELLER : Role.RoleName.ROLE_BUYER)
                .orElseThrow(() -> {
                    log.error("Роль {} не найдена в базе данных", registerRequest.getRole());
                    return new RuntimeException("Роль не найдена");
                });

        user.getRoles().add(role);
        user = userRepository.save(user);

        kafkaService.sendRegisterUser(user);
        notificationService.sendNotification(String.valueOf(user.getId()), "Спасибо за регистрацию)");

        log.info("Регистрация пользователя {} завершена успешно", registerRequest.getPhoneNumber());
    }

    @Transactional
    public AuthResponse login(LoginRequest loginRequest, HttpServletRequest request) {
        log.info("Попытка входа в систему: {}", loginRequest.getPhoneNumber());
        User user;

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getPhoneNumber(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            user = (User) authentication.getPrincipal();
            log.info("Вход выполнен успешно: {}", loginRequest.getPhoneNumber());
        } catch (AuthenticationException e) {
            log.error("Ошибка аутентификации пользователя {}: {}", loginRequest.getPhoneNumber(), e.getMessage());
            throw e;
        }

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        ru.feryafox.jwt.dto.RefreshToken refreshTokenObj = jwtUtils.generateRefreshToken(user.getId());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenObj.getRefreshToken());
        refreshToken.setUser(user);
        refreshToken.setIpAddress(IpAddressUtils.getClientIp(request));
        refreshToken.setExpiredAt(refreshTokenObj.getExpiredAt());

        refreshTokenRepository.save(refreshToken);

        String accessToken = jwtUtils.generateToken(user.getId(), roles);

        log.info("Токены успешно созданы для пользователя {}", loginRequest.getPhoneNumber());
        return new AuthResponse(accessToken, refreshTokenObj.getRefreshToken());
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Обновление токена: {}", refreshToken);
        jwtUtils.validateToken(refreshToken);

        RefreshToken refreshTokenEntity = refreshTokenRepository.findByTokenAndIsLogoutFalse(refreshToken)
                .orElseThrow(() -> new RefreshTokenIsNotExistException(refreshToken));

        User tokenOwner = refreshTokenEntity.getUser();
        List<String> roles = tokenOwner.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        ru.feryafox.jwt.dto.RefreshToken newRefreshToken = jwtUtils.generateRefreshToken(tokenOwner.getId());

        refreshTokenEntity.setToken(newRefreshToken.getRefreshToken());
        refreshTokenEntity.setExpiredAt(newRefreshToken.getExpiredAt());

        refreshTokenRepository.save(refreshTokenEntity);

        log.info("Токен обновлен успешно: {}", refreshToken);
        return new AuthResponse(jwtUtils.generateToken(tokenOwner.getId(), roles), newRefreshToken.getRefreshToken());
    }

    @Transactional
    public void logout(String refreshToken) {
        log.info("Попытка выхода пользователя с токеном {}", refreshToken);

        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RefreshTokenIsNotExistException(refreshToken));

        refreshTokenEntity.setIsLogout(true);
        refreshTokenRepository.save(refreshTokenEntity);

        log.info("Пользователь успешно вышел из системы");
    }

    @Transactional
    public void registerDelivery(RegisterRequestDelivery registerRequestDelivery) {
        log.info("Регистрация доставщика с номером: {}", registerRequestDelivery.getPhoneNumber());

        if (userRepository.existsByPhoneNumber(registerRequestDelivery.getPhoneNumber())) {
            log.warn("Ошибка: доставщик с номером {} уже зарегистрирован", registerRequestDelivery.getPhoneNumber());
            throw new UserIsExistException(registerRequestDelivery.getPhoneNumber());
        }

        User user = new User();
        user.setPhoneNumber(registerRequestDelivery.getPhoneNumber());
        user.setFirstName(registerRequestDelivery.getFirstName());
        user.setSurname(registerRequestDelivery.getSurname());
        user.setMiddleName(registerRequestDelivery.getMiddleName());
        user.setPasswordHash(passwordEncoder.encode(registerRequestDelivery.getPassword()));

        Role role = roleRepository.findByName(Role.RoleName.ROLE_DELIVERY)
                .orElseThrow(() -> {
                    log.error("Ошибка: роль 'DELIVERY' не найдена");
                    return new RuntimeException("Роль не найдена");
                });

        user.getRoles().add(role);
        userRepository.save(user);

        log.info("Регистрация доставщика {} завершена успешно", registerRequestDelivery.getPhoneNumber());
    }
}
