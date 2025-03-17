package ru.feryafox.authservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.feryafox.authservice.entities.User;
import ru.feryafox.authservice.repositories.UserRepository;
import ru.feryafox.authservice.services.CustomUserDetailsService;
import ru.feryafox.utils.UUIDUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setPhoneNumber("+79991234567");
        user.setPasswordHash("hashed_password");
    }

    @Test
    void loadUserByUsername_ValidUUID_ShouldReturnUserDetails() {
        try (var mockStatic = Mockito.mockStatic(UUIDUtils.class)) {
            mockStatic.when(() -> UUIDUtils.isUUID(userId.toString())).thenReturn(true);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId.toString());

            assertNotNull(userDetails);
            assertEquals(user.getPhoneNumber(), userDetails.getUsername());
            assertEquals(user.getPassword(), userDetails.getPassword());

            verify(userRepository, times(1)).findById(userId);
        }
    }

    @Test
    void loadUserByUsername_ValidPhoneNumber_ShouldReturnUserDetails() {
        try (var mockStatic = Mockito.mockStatic(UUIDUtils.class)) {
            mockStatic.when(() -> UUIDUtils.isUUID(user.getPhoneNumber())).thenReturn(false);
            when(userRepository.findByPhoneNumber(user.getPhoneNumber())).thenReturn(Optional.of(user));

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getPhoneNumber());

            assertNotNull(userDetails);
            assertEquals(user.getPhoneNumber(), userDetails.getUsername());
            assertEquals(user.getPassword(), userDetails.getPassword());

            verify(userRepository, times(1)).findByPhoneNumber(user.getPhoneNumber());
        }
    }

    @Test
    void loadUserByUsername_InvalidUUID_ShouldThrowException() {
        try (var mockStatic = Mockito.mockStatic(UUIDUtils.class)) {
            mockStatic.when(() -> UUIDUtils.isUUID(userId.toString())).thenReturn(true);
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername(userId.toString()));

            verify(userRepository, times(1)).findById(userId);
        }
    }

    @Test
    void loadUserByUsername_InvalidPhoneNumber_ShouldThrowException() {
        try (var mockStatic = Mockito.mockStatic(UUIDUtils.class)) {
            mockStatic.when(() -> UUIDUtils.isUUID(user.getPhoneNumber())).thenReturn(false);
            when(userRepository.findByPhoneNumber(user.getPhoneNumber())).thenReturn(Optional.empty());

            assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername(user.getPhoneNumber()));

            verify(userRepository, times(1)).findByPhoneNumber(user.getPhoneNumber());
        }
    }
}
