package ru.feryafox.authservice.services;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.feryafox.authservice.entities.*;
import ru.feryafox.authservice.exceptions.token.RefreshTokenIsNotExistException;
import ru.feryafox.authservice.exceptions.user.UserIsExistException;
import ru.feryafox.authservice.models.requests.LoginRequest;
import ru.feryafox.authservice.models.requests.RegisterRequest;
import ru.feryafox.authservice.models.responses.AuthResponse;
import ru.feryafox.authservice.repositories.RefreshTokenRepository;
import ru.feryafox.authservice.repositories.RoleRepository;
import ru.feryafox.authservice.repositories.UserRepository;
import ru.feryafox.jwt.JwtUtils;
import ru.feryafox.kafka.NotificationService;
import ru.feryafox.authservice.services.kafka.KafkaService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private KafkaService kafkaService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;
    private Role role;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setPhoneNumber("+79991234567");
        registerRequest.setPassword("password");
        registerRequest.setFirstName("Иван");
        registerRequest.setSurname("Петров");
        registerRequest.setMiddleName("Сергеевич");
        registerRequest.setRole("BUYER");

        loginRequest = new LoginRequest();
        loginRequest.setPhoneNumber("+79991234567");
        loginRequest.setPassword("password");

        user = new User();
        user.setId(UUID.randomUUID());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setFirstName(registerRequest.getFirstName());
        user.setSurname(registerRequest.getSurname());
        user.setMiddleName(registerRequest.getMiddleName());
        user.setPasswordHash("hashed_password");

        role = new Role();
        role.setName(Role.RoleName.ROLE_BUYER);

        refreshToken = new RefreshToken();
        refreshToken.setToken("refresh_token_value");
        refreshToken.setUser(user);
    }

    @Test
    void register_NewUser_ShouldRegisterSuccessfully() {
        when(userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())).thenReturn(false);
        when(roleRepository.findByName(any())).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertDoesNotThrow(() -> authService.register(registerRequest));

        verify(userRepository, times(1)).save(any(User.class));
        verify(kafkaService, times(1)).sendRegisterUser(any(User.class));
        verify(notificationService, times(1)).sendNotification(anyString(), anyString());
    }

    @Test
    void register_ExistingUser_ShouldThrowException() {
        when(userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())).thenReturn(true);

        assertThrows(UserIsExistException.class, () -> authService.register(registerRequest));
    }

    @Test
    void login_ValidCredentials_ShouldReturnTokens() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtUtils.generateToken(any(), any())).thenReturn("access_token_value");
        when(jwtUtils.generateRefreshToken(any())).thenReturn(new ru.feryafox.jwt.dto.RefreshToken("refresh_token_value", null));
        when(refreshTokenRepository.save(any())).thenReturn(refreshToken);

        AuthResponse response = authService.login(loginRequest, mock(HttpServletRequest.class));

        assertNotNull(response);
        assertEquals("access_token_value", response.getAccessToken());
        assertEquals("refresh_token_value", response.getRefreshToken());
    }

    @Test
    void refreshToken_ValidToken_ShouldReturnNewTokens() {
        when(jwtUtils.validateToken(anyString())).thenReturn(true);
        when(refreshTokenRepository.findByTokenAndIsLogoutFalse(anyString())).thenReturn(Optional.of(refreshToken));
        when(jwtUtils.generateToken(any(), any())).thenReturn("new_access_token");
        when(jwtUtils.generateRefreshToken(any())).thenReturn(new ru.feryafox.jwt.dto.RefreshToken("new_refresh_token", null));

        AuthResponse response = authService.refreshToken("refresh_token_value");

        assertNotNull(response);
        assertEquals("new_access_token", response.getAccessToken());
        assertEquals("new_refresh_token", response.getRefreshToken());
    }

    @Test
    void refreshToken_InvalidToken_ShouldThrowException() {
        when(jwtUtils.validateToken(anyString())).thenReturn(true);
        when(refreshTokenRepository.findByTokenAndIsLogoutFalse(anyString())).thenReturn(Optional.empty());

        assertThrows(RefreshTokenIsNotExistException.class, () -> authService.refreshToken("invalid_refresh_token"));
    }

    @Test
    void logout_ValidToken_ShouldMarkAsLoggedOut() {
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.of(refreshToken));

        assertDoesNotThrow(() -> authService.logout("refresh_token_value"));

        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }
}
